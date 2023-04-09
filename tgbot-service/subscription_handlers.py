import aiohttp
import requests
import os
from aiogram import types

from db import get_headers
from tg import dp

URL_subscriptions = f"{os.environ.get('SUB_SERVICE', 'http://localhost:8204')}/subscriptions"


async def get_level_status(user_id):
    headers = await get_headers(user_id)

    response = requests.post(URL_subscriptions + "/get", headers=headers,
                             json={"userId": user_id})

    return response.json()


@dp.message_handler(commands=["add_ordinary_subscription"])
async def add_ordinary_subscription(message: types.Message):
    user_id = str(message.from_user.id)
    try:
        old_level = await get_level_status(user_id)
        if old_level == "SECOND_LEVEL":
            await message.answer('У вас уже активирована обычная подписка - 20 запросов в день')
            return
        headers = await get_headers(user_id)
        response = requests.post(URL_subscriptions + "/update", headers=headers,
                                 json={"userId": user_id, "level": "SECOND_LEVEL"})
        response.raise_for_status()
        level = await get_level_status(user_id)
        if level == "SECOND_LEVEL":
            await message.answer('Обычная подписка активирована')
        else:
            await message.answer('Не удалось активировать подписку')
    except requests.exceptions.RequestException as e:
        e_message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {e_message}")


# function to add pro subscription
@dp.message_handler(commands=["add_pro_subscription"])
async def add_pro_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        old_level = await get_level_status(user_id)
        if old_level == "THIRD_LEVEL":
            await message.answer('У вас уже активирована про подписка - неограниченное количество запросов')
            return
        headers = await get_headers(user_id)
        async with aiohttp.ClientSession() as session:
            response = await session.post(URL_subscriptions + "/update", headers=headers,
                                          json={"userId": user_id, "level": "THIRD_LEVEL"})
        level = await get_level_status(user_id)
        if level == "THIRD_LEVEL":
            await message.answer('Про подписка активирована')
        else:
            await message.answer('Не удалось активировать подписку')
    except requests.exceptions.RequestException as e:
        e_message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {e_message}")


# function to delete subscription
@dp.message_handler(commands=["delete_subscription"])
async def delete_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        old_level = await get_level_status(user_id)
        if old_level == "FIRST_LEVEL":
            await message.answer('У вас итак активна только бесплатная подписка, нечего удалять - 1 запрос в день')
            return
        headers = await get_headers(user_id)
        response = requests.post(URL_subscriptions + "/update", headers=headers,
                                 json={"userId": user_id, "level": "FIRST_LEVEL"})
        response.raise_for_status()
        level = await get_level_status(user_id)
        if level == "FIRST_LEVEL":
            await message.answer('Подписка удалена, сейчас активна бесплатная подписка - 1 запрос в день')
        else:
            await message.answer('Не удалось удалить подписку')
    except requests.exceptions.RequestException as e:
        e_message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении операции удаления из подписки: {e_message}")


# function to get subscription status
@dp.message_handler(commands=["subscription_status"])
async def subscription_status(message: types.Message):
    user_id = str(message.from_user.id)
    try:
        level = await get_level_status(user_id)
        subscription_info = await check_subscription_level(level)
        await message.answer(subscription_info)
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при получения статуса подписки: {e}")


async def check_subscription_level(subscription_level):
    # используем свитч-кейс для проверки уровня подписки
    switcher = {
        "FIRST_LEVEL": "Ваша подписка бесплатна - 1 запрос в день",
        "SECOND_LEVEL": "Ваша подписка обычная - 20 запросов в день",
        "THIRD_LEVEL": "Ваша подписка про - без ограничений"
    }
    # возвращаем информацию о подписке в зависимости от уровня
    return switcher.get(subscription_level, "Неправильный уровень подписки")
