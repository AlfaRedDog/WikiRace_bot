from aiogram import Bot, Dispatcher, types
from aiogram.contrib.fsm_storage.memory import MemoryStorage
from aiogram.dispatcher import FSMContext
from aiogram.dispatcher.filters import Command
from aiogram.dispatcher.filters.state import State, StatesGroup
from aiogram.utils import executor
import requests
import aiohttp
import pymongo

# Подключение к MongoDB
mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
mongo_db = mongo_client['mydatabase']
mongo_col = mongo_db['access_tokens']

# create tg bot instance and set token
secret_key = "c3I5JLzXzyUq2Hy9T867JZp0oy2Ppmgg"
# Create a bot instance
bot = Bot(token="6186992746:AAFAhSmWmx6zPTuSUA5A_BcIG8ejQzrUPBc")
storage = MemoryStorage()
# Create a dispatcher instance
dp = Dispatcher(bot, storage=storage)

URL_wikirace = "http://localhost:8206"
URL_subscriptions = "http://localhost:8204"
URL_auth = "http://localhost:8202"


class AddExceptionArticle(StatesGroup):
    waiting_for_article = State()


class DeleteExceptionArticle(StatesGroup):
    waiting_for_article: State = State()


# function to get access token by user_id
async def get_access_token(user_id):
    token = mongo_col.find_one({"user_id": user_id})
    if token:
        return token['access_token']
    else:
        return None


# function checking if message is not empty and if it is not empty, return message
def check_message_one_argument(message):
    if len(message.text.split()) < 1:
        bot.send_message(message.chat.id, "Аргумент пустой")
        return False
    else:
        return True


def check_message_two_arguments(message):
    if len(message.text.split()) < 2:
        bot.send_message(message.chat.id, "Аргумент пустой")
        return False
    elif len(message.text.split()) < 3:
        bot.send_message(message.chat.id, "Аргументы не введены полностью")
        return False
    else:
        return True


class PathState(StatesGroup):
    a = State()
    b = State()


# function to get path from A to B
@dp.message_handler(commands=["path"])
async def get_path(message: types.Message):
    global a, b
    await message.answer("Введите название страницы A:")
    await PathState.a.set()


@dp.message_handler(state=PathState.a)
async def get_path_a(message: types.Message, state: FSMContext):
    global a
    a = message.text
    await message.answer("Введите название страницы B:")
    await PathState.b.set()
    await state.update_data(a=a)


@dp.message_handler(state=PathState.b)
async def get_path_b(message: types.Message, state: FSMContext):
    global a, b
    b = message.text
    async with state.proxy() as data:
        data["b"] = b
        try:
            user_id = str(message.from_user.id)
            access_token = await get_access_token(user_id)
            if not access_token:
                await message.answer('Вы не авторизованы, выполните команду /login')
                return
            headers = {"Authorization": f"Bearer {access_token}"}
            async with aiohttp.ClientSession() as session:
                response = await session.post(URL_wikirace + "/wikirace" + "/get_short_path", headers=headers,
                                             json={"userId": user_id, "startUrl": a, "endUrl": b})

                response.raise_for_status()
                path = (await response.json())["path"]
                await message.answer(path)
        except aiohttp.ClientError as e:
            await message.answer(f"Ошибка при получении пути, не удалось получить путь от {a} до {b}: {e}")

    await state.finish()


@dp.message_handler(Command('add_exception_article'))
async def add_exception_article_handler(message: types.Message):
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
        requests.post(URL_wikirace + "/banned-titles", json={"userId": user_id, "titles": articles})
        await message.answer('Статьи добавлены в список исключений')
    except requests.exceptions.RequestException as e:
        await message.answer(f'Невозможно добавить статьи в список исключений: {e}')
    finally:
        await state.finish()


@dp.message_handler(commands=["delete_exception_article"])
async def delete_exception_article_handler(message: types.Message):
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
        articles = await get_new_exceptions_list(user_id, articles)
        requests.post(URL_wikirace + "/banned-titles", json={"userId": user_id, "titles": articles})
        await message.answer('Статьи добавлены в список исключений')
    except requests.exceptions.RequestException as e:
        await message.answer(f'Невозможно удалить статьи из списка исключений: {e}')
    finally:
        await state.finish()


async def get_deleted_list(user_id: int, list_for_delete: list):
    try:
        exceptions_list = await get_exception_list(user_id)
        # exceptions_list - list_for_delete = list of articles that were not deleted
        exceptions_list = [article for article in exceptions_list if article not in list_for_delete]
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"


async def get_new_exceptions_list(user_id: int, list_for_add: list):
    try:
        exceptions_list = await get_exception_list(user_id)
        # adding new unique values
        exceptions_list = list(set(exceptions_list) | set(list_for_add))
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"


@dp.message_handler(commands=["registration"])
# function to register user
async def registration(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        response = requests.post(URL_auth + "/users",
                                 json={"username": user_id, "password": secret_key})
        response.raise_for_status()
        # send message to user
        await message.answer("Пользователь зарегистрирован")
    except requests.exceptions.HTTPError as e:
        # если код ответа сервера >= 400, то возникает исключение HTTPError
        await message.answer(f"Произошла ошибка при выполнении операции регистрации: {e}")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении операции регистрации: {e}")


# function to login user
@dp.message_handler(commands=["login"])
async def login(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    isLogin = await login_user(user_id)
    if isLogin:
        # send message to user
        await message.answer("Пользователь авторизован")
    else:
        await message.answer("Произошла ошибка при выполнении операции входа")


# function to login user
async def login_user(user_id):
    try:
        response = requests.post(URL_auth + "/authentication",
                                 json={"username": user_id, "password": secret_key})
        response.raise_for_status()  # генерирует исключение, если код ответа сервера >= 400
        access_token = response.json().get("accessToken")
        # save access_token to MongoDB with user_id as key
        mongo_col.update_one({'user_id': user_id}, {'$set': {'access_token': access_token}}, upsert=True)
        return True
    except requests.exceptions.HTTPError as e:
        # если код ответа сервера >= 400, то возникает исключение HTTPError
        print(f"Произошла ошибка при выполнении операции входа: {e}")
        return False
    except requests.exceptions.RequestException as e:
        # если произошла какая-то другая ошибка при выполнении запроса, то возникает исключение RequestException
        print(f"Произошла ошибка при выполнении операции входа: {e}")
        return False


@dp.message_handler(commands=["watch_list_exceptions"])
async def watch_list_exceptions(message: types.Message):
    user_id = message.from_user.id
    try:
        await message.answer(await get_exception_list(user_id))
    except requests.exceptions.RequestException as e:
        await message.answer(f"Ошибка при получении списка исключений. Попробуйте позже.: {e}")


async def get_exception_list(user_id):
    try:
        response = requests.get(URL_wikirace + "/banned-titles" + f"/{user_id}")
        response.raise_for_status()
        exceptions_list = response.json()
        return exceptions_list
    except requests.exceptions.RequestException as e:
        return f"Ошибка при получении списка исключений. Попробуйте позже.: {e}"


@dp.message_handler(commands=["add_ordinary_subscription"])
async def add_ordinary_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        requests.post(URL_subscriptions + "/subscriptions", json={"userId": user_id, "level": "FIRST_LEVEL"})
        await message.answer("Обычная подписка активирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {e}")


# function to add pro subscription
@dp.message_handler(commands=["add_pro_subscription"])
async def add_pro_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        requests.post(URL_subscriptions + "/subscriptions", json={"userId": user_id, "level": "SECOND_LEVEL"})
        await message.answer("Про подписка активирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении операции добавления в подписки: {e}")


# function to delete subscription
@dp.message_handler(commands=["delete_subscription"])
async def delete_subscription(message: types.Message):
    if check_message_one_argument(message) is False:
        return
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        requests.post(URL_subscriptions + "/subscriptions", json={"userId": user_id, "level": "THIRD_LEVEL"})
        await message.answer("Подписка деактивирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении операции удаления из подписки: {e}")


# start bot
if __name__ == '__main__':
    executor.start_polling(dp, skip_updates=True)
