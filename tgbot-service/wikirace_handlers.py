import aiohttp
import requests
import os
from aiogram import types
from aiogram.dispatcher import FSMContext
from aiogram.dispatcher.filters import Command
from aiogram.dispatcher.filters.state import State, StatesGroup
import asyncio


from db import get_headers
from graph import create_graph
from tg import dp

URL_wikirace = os.environ.get('WIKI_RACE_SERVICE', 'http://localhost:8206')


class AddExceptionArticle(StatesGroup):
    waiting_for_article = State()


class DeleteExceptionArticle(StatesGroup):
    waiting_for_article: State = State()


class PathState(StatesGroup):
    a = State()
    b = State()


@dp.message_handler(commands=["get_path"])
async def get_path(message: types.Message):
    await message.answer("Введите название страницы A:")
    await PathState.a.set()


@dp.message_handler(state=PathState.a)
async def get_path_a(message: types.Message, state: FSMContext):
    await state.update_data(a=message.text)
    await message.answer("Введите название страницы B:")
    await PathState.b.set()


@dp.message_handler(state=PathState.b)
async def get_path_b(message: types.Message, state: FSMContext):
    try:
        async with state.proxy() as data:
            a = data['a']
            b = message.text
            user_id = str(message.from_user.id)
            headers = await get_headers(user_id)
            await message.answer("Ищем путь...")
            response = requests.post(
                URL_wikirace + "/wikirace" + "/get_short_path",
                headers=headers,
                json={"userId": user_id, "startUrl": a, "endUrl": b},
                timeout=200)
            response.raise_for_status()
            path = response.json().get("path")
            if path is not None:
                await create_graph(path)
                await message.answer_photo(types.InputFile("path.png"), caption=path)
            else:
                await message.answer("Вы достигли лимита для вашей подписки")
    except asyncio.TimeoutError:
        await message.answer("Поиск пути занял слишком много времени. Попробуйте еще раз.")
    except requests.exceptions.RequestException as e:
        e_message = response.json().get("message")
        await message.answer(f"Ошибка при получении пути, не удалось получить путь от {a} до {b}: {e_message}")
    finally:
        # Сбрасываем состояние
        await state.finish()


@dp.message_handler(commands=["watch_list_exceptions"])
async def watch_list_exceptions(message: types.Message):
    user_id = str(message.from_user.id)
    try:
        exception_list = await get_exception_list(user_id)
        await message.answer(exception_list)
    except requests.exceptions.RequestException as e:
        await message.answer(f"Ошибка при получении списка исключений. Попробуйте позже.: {e}")


@dp.message_handler(commands=["delete_exception_article"])
async def delete_exception_article(message: types.Message):
    await message.answer('Введите список статей через запятую, которые нужно удалить:')
    await DeleteExceptionArticle.waiting_for_article.set()


@dp.message_handler(state=DeleteExceptionArticle.waiting_for_article)
async def get_exception_articles_for_delete_handler(message: types.Message, state: FSMContext):
    articles = message.text.split(',')
    articles = [article.strip() for article in articles if article.strip()]
    if not articles:
        await message.answer('Вы ввели пустой список статей, попробуйте ещё раз')
        return
    user_id = str(message.from_user.id)
    try:
        articles = await get_deleted_list(user_id, articles)
        headers = await get_headers(user_id)
        requests.post(URL_wikirace + "/banned-titles", headers=headers, json={"userId": user_id, "titles": articles})
        new_exceptions = await get_exception_list(user_id)
        if len(new_exceptions) == len(articles):
            await message.answer('Статьи удалены из списка исключений')
        else:
            await message.answer('Не удалось удалить статьи из список исключений')
    except requests.exceptions.RequestException as e:
        await message.answer(f'Невозможно удалить статьи из списка исключений: {e}')
    finally:
        await state.finish()


@dp.message_handler(Command('add_exception_article'))
async def add_exception_article(message: types.Message):
    await message.answer('Введите список статей через запятую:')
    await AddExceptionArticle.waiting_for_article.set()


@dp.message_handler(state=AddExceptionArticle.waiting_for_article)
async def get_exception_articles_handler(message: types.Message, state: FSMContext):
    articles = message.text.split(',')
    articles = [article.strip() for article in articles if article.strip()]
    if not articles:
        await message.answer('Вы ввели пустой список статей, попробуйте ещё раз')
        return
    user_id = str(message.from_user.id)

    try:
        articles = await get_new_exceptions_list(user_id, articles)
        headers = await get_headers(user_id)
        requests.post(URL_wikirace + "/banned-titles", headers=headers, json={"userId": user_id, "titles": articles})
        new_exceptions = await get_exception_list(user_id)
        if len(new_exceptions) == len(articles):
            await message.answer('Статьи добавлены в список исключений')
        else:
            await message.answer('Не удалось добавить статьи в список исключений')
    except requests.exceptions.RequestException as e:
        await message.answer(f'Невозможно добавить статьи в список исключений: {e}')
    finally:
        await state.finish()


async def get_exception_list(user_id):
    try:
        headers = await get_headers(user_id)
        response = requests.get(URL_wikirace + "/banned-titles" + f"/{user_id}", headers=headers)
        response.raise_for_status()
        exceptions_list = response.json()
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"


async def get_deleted_list(user_id: str, list_for_delete: list):
    try:
        exceptions_list = await get_exception_list(user_id)
        # exceptions_list - list_for_delete = list of articles that were not deleted
        exceptions_list = [article for article in exceptions_list if article not in list_for_delete]
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"


async def get_new_exceptions_list(user_id: str, list_for_add: list):
    try:
        exceptions_list = await get_exception_list(user_id)
        # adding new unique values
        exceptions_list = list(set(exceptions_list) | set(list_for_add))
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"
