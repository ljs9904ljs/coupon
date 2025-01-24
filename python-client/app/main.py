from fastapi import FastAPI
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
import uvicorn
import asyncio


BOOTSTRAP_SERVERS = 'kafka1:9092,kafka2:9094,kafka3:9096'

app = FastAPI()

# Kafka producer configuration
producer = AIOKafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS)
consumer = AIOKafkaConsumer(
        'response-topic',
        bootstrap_servers=BOOTSTRAP_SERVERS,
        group_id='fastapi_group'
)

async def consume():
    
    await consumer.start()
    try:
        async for msg in consumer:
            print(f"Received: {msg.value.decode('utf-8')}")
            # Process the message here
    finally:
        await consumer.stop()

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

@app.get("/")
def read_root():
    return {"Hello": "World"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=7000, reload=True)