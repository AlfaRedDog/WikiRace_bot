import pymongo

mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
mongo_db = mongo_client['mydatabase']
mongo_col = mongo_db['access_tokens']


# function to get access token by user_id
async def get_access_token(user_id):
    token = mongo_col.find_one({"user_id": user_id})
    if token:
        return token['access_token']
    else:
        return None
