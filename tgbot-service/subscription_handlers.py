import requests

from common_functions import get_headers
from tg import dp, check_message_one_argument
from aiogram import types

URL_subscriptions = "http://localhost:8204"


@dp.message_handler(commands=["add_ordinary_subscription"])
async def add_ordinary_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        headers = await get_headers(user_id)
        response = requests.post(URL_subscriptions + "/subscriptions", headers=headers,
                                 json={"userId": user_id, "level": "FIRST_LEVEL"})
        response.raise_for_status()  # генерирует исключение, если код ответа сервера >= 400
        level = response.json().get("level")
        if level == "FIRST_LEVEL":
            await message.answer('Обычная подписка активирована')
        else:
            await message.answer('Не удалось активировать подписку')
    except requests.exceptions.RequestException as e:
        message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {message}")


# function to add pro subscription
@dp.message_handler(commands=["add_pro_subscription"])
async def add_pro_subscription(message: types.Message):
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        headers = await get_headers(user_id)
        response = requests.post(URL_subscriptions + "/subscriptions", headers=headers,
                                 json={"userId": user_id, "level": "SECOND_LEVEL"})
        response.raise_for_status()  # генерирует исключение, если код ответа сервера >= 400
        level = response.json().get("level")
        if level == "SECOND_LEVEL":
            await message.answer('Про подписка активирована')
        else:
            await message.answer('Не удалось активировать подписку')
    except requests.exceptions.RequestException as e:
        message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении активации подписки: {message}")


# function to delete subscription
@dp.message_handler(commands=["delete_subscription"])
async def delete_subscription(message: types.Message):
    if check_message_one_argument(message) is False:
        return
    # get user_id by telegram user_id
    user_id = str(message.from_user.id)
    try:
        headers = await get_headers(user_id)
        response = requests.post(URL_subscriptions + "/subscriptions", headers=headers,
                                 json={"userId": user_id, "level": "THIRD_LEVEL"})

        level = response.json().get("level")
        if level == "THIRD_LEVEL":
            await message.answer('Подписка удалена')
        else:
            await message.answer('Не удалось удалить подписку')
    except requests.exceptions.RequestException as e:
        message = response.json().get("message")
        await message.answer(f"Произошла ошибка при выполнении операции удаления из подписки: {message}")
