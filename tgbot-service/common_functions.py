from db import get_access_token


async def get_headers(user_id):
    access_token = await get_access_token(user_id)
    if not access_token:
        return None
    return {"Authorization": f"Bearer {access_token}"}