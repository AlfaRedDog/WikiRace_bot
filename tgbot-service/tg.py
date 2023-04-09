# function checking if message is not empty and if it is not empty, return message
# create tg bot instance and set token
from aiogram import Bot, Dispatcher
from aiogram.contrib.fsm_storage.memory import MemoryStorage

# Create a bot instance
bot = Bot(token="6186992746:AAFAhSmWmx6zPTuSUA5A_BcIG8ejQzrUPBc")
# bot = Bot(token="5808675049:AAEAhaIA-kUuQR2mQ7Z96e4oodngzrY0KCo")
storage = MemoryStorage()
# Create a dispatcher instance
dp = Dispatcher(bot, storage=storage)


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
