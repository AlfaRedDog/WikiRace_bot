import requests
import os
from aiogram import types

# Подключение к MongoDB
from db import mongo_col
from tg import dp

secret_key = "c3I5JLzXzyUq2Hy9T867JZp0oy2Ppmgg"
URL_auth = os.environ.get('AUTH_SERVICE', 'http://localhost:8202')


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


@dp.message_handler(commands=["start"])
# function to register user
async def start(message: types.Message):
    user_id = str(message.from_user.id)
    try:
        #registration
        response = requests.post(URL_auth + "/users",
                                 json={"username": user_id, "password": secret_key})
        response.raise_for_status()

        isLogin = await login_user(user_id)
        if isLogin:
            # send message to user
            await message.answer("Пользователь авторизован")
        else:
            await message.answer("Произошла ошибка при выполнении операции входа")
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
