
- 개요
    - 스프링부트로 REST API 제작 + 파이썬으로 API 호출

- 폴더와 파일에 대한 간단한 설명
    - python-client 폴더
        - 스프링부트의 API를 coroutine(async/await)을 활용하여 호출하도록 하는 코드가 작성되어 있다.
    - docker-compose.yml 파일
        - 5개의 Redis Node로 구성된 Redis cluster와 스프링부트를 연결한다.
    - docker-compose_onenode.yml 파일
        - 1개의 단일 Redis와 스프링부트를 연결한다.


- [구성 방식] 단일 노드 레디스 vs. 레디스 클러스터(5개 노드)
    1. 단일 노드 레디스
        - 선착순 쿠폰 10000개를 레디스에 저장해두고, API 요청마다 Redis decrement 연산을 활용하여 쿠폰을 발급한다.
    2. 레디스 클러스터(5개 노드)
        - Hash Tag를 활용하여 선착순 쿠폰을 2000개씩 각 노드에 분산해서 저장해두고, API 요청마다 랜덤한 Hash Tag 값을 할당하여 부하를 분산한다. 쿠폰 발행 방식은 '단일 노드 레디스'와 마찬가지로 Redis decrement 연산을 활용한다.


- 실험 결과
    - <결론>
        - 동일 cpu, memory 기준에서는 단일 Redis 노드로 수행하는 것이 비슷하거나 약간 더 나았다.
        - Redis 노드마다 cpu/memory 자원량을 똑같이 세팅해놨으니 분명 어느 시점부터는 redis cluster가 유리해야 맞을 것이다.
        - 그런데 그 지점을 찾을 수가 없다. 파이썬 aiohttp로 요청을 보내는 방식이 잘못되었나? 다른 스트레스 테스트 도구를 사용해보아야 맞을까?

===================================================================

- CPU/Memory 세팅
    - CPU 종류: 12th Gen Intel(R) Core(TM) i7-12700H
    - 코어: 14개
    - 논리 프로세서: 20개
    - docker-compose를 활용하여 각 컨테이너마다 cpu, memory를 한정시켰음.
    - Redis node 1개 당: 1cpu, 128M mem

- <Redis node 1개만 사용, 1000개의 requests, 딱 1회만 실행(캐싱 방지)>
    - 선착순 쿠폰 10000개
        - spring boot: 1cpu, 1024M mem -> 40TPS, spring boot 실행시간 17초
        - spring boot: 1cpu, 2048M mem -> 70TPS, spring boot 실행시간 18초
        - spring boot: 8cpu, 1024M mem -> 440TPS, spring boot 실행시간 12초, 번외) 10000개 기준 1126TPS, 아예 다시 실행해봤더니 1000개 기준 547TPS..?
        - spring boot: 8cpu, 2048M mem -> 400TPS, spring boot 실행시간 11초
        - spring boot: 8cpu, 4096M mem -> 500TPS, spring boot 실행시간 13초
        - spring boot: 16cpu, 1024M mem -> 220TPS, spring boot 실행시간 12초
        - spring boot: 16cpu, 4096M mem -> 310TPS, spring boot 실행시간 12초
        - spring boot: 14cpu, 1024M mem -> 505TPS, spring boot 실행시간 13초
        - spring boot: 14cpu, 4096M mem -> 420TPS, spring boot 실행시간 12초


- <Redis node 5개를 cluster로 사용, 1000개 || 10000개의 requests, 딱 1회만 실행(캐싱 방지)>
    - 선착순 쿠폰 10000개
    - lettuce pool을 5개로 고정해놓고 실험했음.
    - 들어온 request에 랜덤하게 5개의 hash tag 중 하나를 붙이는 식으로 부하를 분산했음. 그래서 선착순 쿠폰이 남아 있어도 실패할 수도 있다.

        - spring boot: 8cpu, 1024M mem -> 440TPS || 1216TPS, spring boot 실행시간 13초, 51실패(10000개 중)
        - spring boot: 8cpu, 4096M mem -> 364TPS || 1135TPS, spring boot 실행시간 12초, 60실패(10000개 중)
        - spring boot: 14cpu, 1024M mem -> 466TPS || 921TPS, spring boot 실행시간 13초, 100실패(10000개 중)
