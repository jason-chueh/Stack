from youtubesearchpython import VideosSearch
import json
def youtubeSearch(exercise):
    videosSearch = VideosSearch(exercise, limit = 10)
    json_string = json.dumps(videosSearch.result()["result"])
    return json_string
