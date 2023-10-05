from youtube_transcript_api import YouTubeTranscriptApi

def getTranscript(id):

    a = YouTubeTranscriptApi.get_transcript(id)

    result = ""
    for i in a:
        result = result + i['text']

    return result