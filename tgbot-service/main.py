from aiogram import Bot, Dispatcher, types
from aiogram.contrib.fsm_storage.memory import MemoryStorage
from aiogram.dispatcher import FSMContext
from aiogram.dispatcher.filters import Command
from aiogram.dispatcher.filters.state import State, StatesGroup
from aiogram.utils import executor
import requests

# create tg bot instance and set token
secret_key = "c3I5JLzXzyUq2Hy9T867JZp0oy2Ppmgg"
# Create a bot instance
bot = Bot(token="6186992746:AAFAhSmWmx6zPTuSUA5A_BcIG8ejQzrUPBc")
storage = MemoryStorage()
# Create a dispatcher instance
dp = Dispatcher(bot, storage=storage)


class AddExceptionArticle(StatesGroup):
    waiting_for_article = State()


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
        # Получаем путь от API
        try:
            response = await requests.get("http://localhost:8202/api/path", data={"A": a, "B": b})
            response.raise_for_status()
            path = response.json()["path"]
            await message.answer(path)
        except requests.exceptions.RequestException as e:
            await message.answer(f"Ошибка при получении пути, не удалось получить путь от {a} до {b}: {e}")

    await state.finish()


@dp.message_handler(Command('add_exception_article'))
async def add_exception_article_handler(message: types.Message):
    await message.answer('Введите статью исключение:')
    await AddExceptionArticle.waiting_for_article.set()


@dp.message_handler(state=AddExceptionArticle.waiting_for_article)
async def get_exception_article_handler(message: types.Message, state: FSMContext):
    article = message.text
    if not article:
        await message.answer('Вы ввели пустую статью, попробуйте ещё раз')
        return
    user_id = message.from_user.id
    try:
        requests.put("http://localhost:8202/api/add_exception_article", data={"user_id": user_id, "article": article})
        await message.answer('Статья добавлена в список исключений')
    except requests.exceptions.RequestException as e:
        await message.answer(f'Невозможно добавить статью в список исключений: {e}')
    finally:
        await state.finish()


@dp.message_handler(commands=["delete_exception_article"])
# function to delete exception article
async def delete_exception_article(message: types.Message):
    await bot.send_message(message.chat.id, 'Введите статью исключение для удаления:')
    # Ждём следующий ввод от пользователя
    await bot.register_next_step_handler(message, get_exception_article_for_delete)


async def get_exception_article_for_delete(message: types.Message):
    article = message.text
    # проверяем на пустоту введённое сообщение
    if not article:
        return
    # get user_id by telegram user_id
    user_id = message.from_user.id
    try:
        # Удаляем статью из API
        response = requests.delete("http://localhost:8202/api/delete_exception_article",
                                   data={"user_id": user_id, "article": article})

        # Проверяем код ответа
        if response.status_code == 200:
            await bot.send_message(message.chat.id, "Статья удалена из списка исключений")
        else:
            await bot.send_message(message.chat.id, "Произошла ошибка при выполнении операции удаления")
    except requests.exceptions.RequestException as e:
        await bot.send_message(message.chat.id, f"Произошла ошибка при выполнении операции удаления: {e}")


@dp.message_handler(commands=["registration"])
# function to register user
async def registration(message: types.Message):
    # get user_id by telegram user_id
    user_id = message.from_user.id
    # put user_id to API
    try:
        response = requests.put("http://localhost:8202/api/registration",
                                data={"user_id": user_id, "secret_key": secret_key})
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
    user_id = message.from_user.id
    try:
        response = requests.put("http://localhost:8202/api/authentication",
                                data={"user_id": user_id, "secret_key": secret_key})
        response.raise_for_status()  # генерирует исключение, если код ответа сервера >= 400
        # send message to user
        await message.answer("Пользователь авторизован")
    except requests.exceptions.HTTPError as e:
        # если код ответа сервера >= 400, то возникает исключение HTTPError
        await message.answer(f"Произошла ошибка при выполнении операции входа: {e}")
    except requests.exceptions.RequestException as e:
        # если произошла какая-то другая ошибка при выполнении запроса, то возникает исключение RequestException
        await message.answer(f"Произошла ошибка при выполнении операции входа: {e}")


@dp.message_handler(commands=["watch_list_exceptions"])
async def watch_list_exceptions(message: types.Message):
    user_id = message.from_user.id
    try:
        exception_list = requests.get("http://localhost:8202/api/watch_list_exceptions",
                                      data={"user_id": user_id, "secret_key": secret_key}).json()
        exception_list = exception_list["exception_list"]
        await message.answer(exception_list)
    except requests.exceptions.RequestException as e:
        await message.answer(f"Ошибка при получении списка исключений. Попробуйте позже.: {e}")


@dp.message_handler(commands=["add_ordinary_subscription"])
async def add_ordinary_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = message.from_user.id
    # put article to API
    try:
        requests.put("http://localhost:8202/api/add_subscription", data={"user_id": user_id, "subscription_type": 0})
        await message.answer("Обычная подписка активирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {e}")


# function to add pro subscription
@dp.message_handler(commands=["add_pro_subscription"])
async def add_pro_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = message.from_user.id
    try:
        requests.put("http://localhost:8202/api/add_subscription", data={"user_id": user_id, "subscription_type": 1})
        await message.answer("Про подписка активирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении операции добавления в подписки: {e}")


# function to delete subscription
@dp.message_handler(commands=["delete_subscription"])
async def delete_subscription(message: types.Message):
    if check_message_one_argument(message) is False:
        return
    # get user_id by telegram user_id
    user_id = message.from_user.id
    try:
        requests.put("http://localhost:8202/api/add_subscription", data={"user_id": user_id, "subscription_type": 2})
        await message.answer("Подписка деактивирована")
    except requests.exceptions.RequestException as e:
        await message.answer(f"Произошла ошибка при выполнении операции удаления из подписки: {e}")

# start bot
if __name__ == '__main__':
    executor.start_polling(dp, skip_updates=True)
