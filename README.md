# Mars One Project

# Explorando Marte

Um conjunto de sondas foi enviado pela NASA à Marte e irá pousar num planalto.
Esse planalto, que curiosamente é retangular, deve ser explorado pelas sondas para que suas câmera embutidas consigam ter uma visão completa da área e enviar as imagens de volta para a Terra.

A posição e direção de uma sonda são representadas por uma combinação de coordenadas x-y e uma letra representando a direção cardinal para qual a sonda aponta, seguindo a rosa dos ventos em inglês.

![rosa dos ventos](http://i.imgur.com/li8Ae5L.png "Rosa dos Ventos")

O planalto é divido numa malha para simplificar a navegação. Um exemplo de posição seria (0, 0, N), que indica que a sonda está no canto inferior esquerdo e apontando para o Norte.

Para controlar as sondas, a NASA envia uma simples sequência de letras. As letras possíveis são "L", "R" e "M". Destas, "L" e "R" fazem a sonda virar 90 graus para a esquerda  ou direita, respectivamente, sem mover a sonda. "M" faz com que a sonda mova-se para a frente um ponto da malha, mantendo a mesma direção.

Nesta malha o ponto ao norte de (x,y) é sempre (x, y+1).

Você deve fazer um programa que processe uma série de instruções enviadas para as sondas que estão explorando este planalto.
O formato da entrada e saída deste programa segue abaixo.

A forma de entrada e saída dos dados fica à sua escolha.

# API

### **PLATEAU**
##### **METHOD: POST**
```
/api/v1/plateau
```

##### **Body**

```json
{
    "x": 10,
    "y": 10
}
```

##### **Success Response:**

```
Code: 201 CREATED
Content: None
```

##### **Error Response:**
```
Code: 400 BAD REQUEST 
Content: { "message": "Plateau is already set" }
``` 

##### **Sample Call:**
```
curl -H "Content-Type: application/json" -X POST -d '{"x": 10, "y": 10}' http://localhost:9000/api/v1/plateau
```
##### **METHOD: GET**
```
/api/v1/plateau
```


##### **Success Response:**

```
Code: 200 CREATED
Content: { "x": 10, "y": 10 }
```

##### **Error Response:**
```
Code: 404 NOT FOUND 
Content: None
``` 

##### **Sample Call:**
```
curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/v1/plateau
```

#### **ROVERS**
##### **METHOD: POST**
```
/api/v1/rovers
```

##### **Body**

```json
{
  "id": 1,
  "cardinalDirection": {
      "type": "South"
  },
  "x": 1,
  "y": 1
}
```

##### **Success Response:**

```
Code: 201 CREATED
Content: None
```

##### **Errors Response:**
```
Code: 400 BAD REQUEST 
Content: { "message": "Could not deploy Rover: Rover with id=1 has already been deployed" }
```

```
Code: 412 PRECONDITION FAILED 
Content: { "message": "Could not deploy Rover: Plateau is not set" }
```  

##### **Sample Call:**
```
curl -H "Content-Type: application/json" -X POST -d '{ "id": 1, "cardinalDirection": { "type": "South" }, "x": 1, "y": 1 }' http://localhost:9000/api/v1/roversg
```
##### **METHOD: GET**
```
/api/v1/rovers
```

##### **Success Response:**

```
Code: 200 CREATED
Content: {
             "rovers": [
                 {
                     "id": 1,
                     "cardinalDirection": {
                         "type": "South"
                     },
                     "x": 1,
                     "y": 1
                 },
                 {
                     "id": 2,
                     "cardinalDirection": {
                         "type": "South"
                     },
                     "x": 1,
                     "y": 1
                 }
             ]
         }
```

##### **Errors Response:**
```
Code: 412 PRECONDITION FAILED 
Content: { "message": "Could not deploy Rover: Plateau is not set" }
```  

##### **Sample Call:**
```
curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/v1/rovers
```

#### **ROVERS COMMANDS**
##### **METHOD: POST**
```
/api/v1/rovers/{id}/commands
```

##### **Body**

```json
["M", "R", "M", "L"]
```

##### **Success Response:**

```
Code: 202 ACCEPTED
Content: None
```

##### **Errors Response:**
```
Code: 404 NOT FOUND
Content: None
```

##### **Sample Call:**
```
curl -H "Content-Type: application/json" -X POST -d '["M", "R", "M", "L"]' http://localhost:9000//api/v1/rovers/1/commands
```
