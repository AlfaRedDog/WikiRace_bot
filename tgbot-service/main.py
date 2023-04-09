# Подключение к MongoDB
from aiogram.utils import executor

from subscription_handlers import add_ordinary_subscription, add_pro_subscription, delete_subscription, subscription_status
from tg import dp
from wikirace_handlers import get_path, watch_list_exceptions, delete_exception_article, add_exception_article
from auth_hadlers import registration, login, start

dp.register_message_handler(registration, commands=["registration"])
dp.register_message_handler(login, commands=["login"])
dp.register_message_handler(start, commands=["start"])

dp.register_message_handler(get_path, commands=["get_path"])
dp.register_message_handler(watch_list_exceptions, commands=["watch_list_exceptions"])
dp.register_message_handler(delete_exception_article, commands=["delete_exception_article"])
dp.register_message_handler(add_exception_article, commands=["add_exception_article"])

dp.register_message_handler(add_ordinary_subscription, commands=["add_ordinary_subscription"])
dp.register_message_handler(add_pro_subscription, commands=["add_pro_subscription"])
dp.register_message_handler(delete_subscription, commands=["delete_subscription"])
dp.register_message_handler(subscription_status, commands=["subscription_status"])

# start bot
if __name__ == '__main__':
    executor.start_polling(dp, skip_updates=True)
