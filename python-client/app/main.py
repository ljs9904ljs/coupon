from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse

from collections import defaultdict

from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
import uvicorn
import asyncio
import json
from pydantic import BaseModel, Field
from sse_starlette.sse import EventSourceResponse



BOOTSTRAP_SERVERS = 'kafka1:9092,kafka2:9094,kafka3:9096'

app = FastAPI()
sse_conn = dict()

# Kafka producer configuration
producer = AIOKafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS)
consumer = AIOKafkaConsumer(
        'response-topic',
        bootstrap_servers=BOOTSTRAP_SERVERS,
        group_id='fastapi_group'
)


class Res(BaseModel):
    code: int
    client_id: str | None = Field(alias="clientId")
    num: int | None

async def consume():
    print("consume event starts!!!")
    await consumer.start()
    try:
        async for msg in consumer:
            res= Res.parse_raw(msg.value.decode('utf-8'))
            print(f"Received: {res}")
            await send_sse_message(res.client_id, msg.value.decode('utf-8'))
            # Process the message here
    finally:
        await consumer.stop()


async def event_generator(request: Request, client_id: str):
    
    try:
        while True:
            if await request.is_disconnected():
                break
            
            # Yield an empty dict to keep the connection alive
            # yield "just stiring!@$@%!%$@#$^@#$"
            # yield {
            #     "data": "content is empty~!@~!@~!@~@~"
            # }
            if client_id in sse_conn:
                
                # 타임아웃을 설정한 이유? 
                    # -> FastAPI auto reload를 사용하면서,
                    # postman으로 SSE connection 생성해둔 다음에 reloading하면 
                    # connection이 제대로 종료되지 않았기 때문에 FastAPI 쪽에서 오류가 발생한다.
                event = await asyncio.wait_for(sse_conn[client_id].get(), timeout=10)  # 10초
                print("이벤트 발사~~~~!!!!")
                yield f"data: {event}\n\n"
                break  # SSE connection 종료!
            else:
                yield f"data: no data now!!!!!!"
            
            await asyncio.sleep(1)  # Adjust the interval as needed
    finally:
        del sse_conn[client_id]
    


async def send_sse_message(client_id, message):
    if client_id in sse_conn:
        await sse_conn[client_id].put(message)


@app.on_event("startup")
async def startup_event():
    asyncio.create_task(consume())
    await producer.start()


@app.on_event("shutdown")
async def shutdown_event():
    await producer.stop()


@app.post("/send_message")
async def send_message(topic: str, message: str):
    await producer.send_and_wait(topic, message.encode('utf-8'))
    return {"status": "Message sent successfully"}


@app.get("/stream/{client_id}")
async def message_stream(request: Request, client_id: str):
    # producer.send_and_wait('mytopic', client_id.encode('utf-8'))
    sse_conn[client_id] = asyncio.Queue()
    
    await producer.send_and_wait('mytopic', client_id.encode('utf-8'))
    # return StreamingResponse(event_generator(request, client_id), media_type="text/event-stream")
    return EventSourceResponse(event_generator(request, client_id))


@app.get("/")
def read_root():
    return {"Hello": "World"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=7000, reload=True)