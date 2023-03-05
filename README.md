# DailyReminder

Uma API para controle de atividades diárias.

---

## Endpoints

- Atividade 
    - [Cadastrar](#cadastrar-atividade)
    - [Mostrar Detalhes](#detalhar-atividade)
    - [Atualizar](#atualizar-atividade)
    - [Deletar](#deletar-atividade)
    - [Mostrar Todos](#detalhar-todas-atividades)
- Dias
    - [Cadastrar](#cadastrar-dias)
    - [Mostrar Detalhes](#detalhar-dias)
    - [Atualizar](#atualizar-dias)
    - [Deletar](#deletar-dias)
    - [Mostrar Todos](#detalhar-todos-dias)
- Semanas
    - [Cadastrar](#cadastrar-semanas)
    - [Mostrar Detalhes](#detalhar-semanas)
    - [Atualizar](#atualizar-semanas)
    - [Deletar](#deletar-semanas)
    - [Mostrar Todos](#detalhar-todas-semanas)

---

## Cadastrar Atividade

`POST` /api/x


**Campos de Requisição**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|:-------------:|-----------|
| atividade_id | int | Sim | O código da atividade feita. |
| atividade | texto | Sim | Um nome para uma atividade a ser realizada de no máximo 50 caracteres. |
| data | Data | Sim | A data da atividade. |
| duracao | DateTime | Não | A duração do tempo da atividade. |
| semana_atividade | int | Sim | O número relativo ao dia da semana. |


**Exemplo de Corpo de Requisição**

```js
{
    atividade_id: 1,
    atividade: 'Treinar',
    dia: '2023-03-04',
    duracao: 01:30:00,
    semana_atividade: [
        1: 'segunda-feira',
        2: 'terça-feira',
        3: 'quarta-feira',
        4: 'quinta-feira',
        5: 'sexta-feira',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Despesa criada com sucesso. | 
| 400 | Os campos enviados são inválidos. |

---

## Detalhar Atividade

`GET` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    atividade_id: 1,
    atividade: 'Treinar',
    dia: '2023-03-04',
    duracao: 01:30:00,
    semana_atividade: [
        'segunda-feira',
        'terça-feira',
        'quarta-feira',
        'quinta-feira',
        'sexta-feira',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. |
| 204 | Sem conteúdo. |
| 404 | Não existe despesa com o id informado. |

---

## Atualizar Atividade

`PATCH` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    atividade_id: 2,
    atividade: 'Estudar',
    dia: '2023-03-04',
    duracao: 02:30:00,
    semana_atividade: [
        'terça-feira',
        'quarta-feira',
        'quinta-feira',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Dado atualizado com sucesso. | 
| 404 | Não existe despesa com o id informado. |
| 406 | Dado inválido ou errado. |

## Deletar Atividade

`DELETE` /api/x/{id}

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dado deletado com sucesso. | 
| 404 | Não existe despesa com o id informado. |


## Detalhar Todas Atividades

`GET` /api/x/

**Exemplo de Corpo de Requisição**

```js
{
    [
        {
            atividade_id: 1,
            atividade: 'Treinar',
            dia: '2023-03-04',
            duracao: 01:30:00,
            semana_atividade: [
                'segunda-feira',
                'terça-feira',
                'quarta-feira',
                'quinta-feira',
                'sexta-feira',
            ]
        },
        {
            atividade_id: 2,
            atividade: 'Estudar',
            dia: '2023-03-04',
            duracao: 02:30:00,
            semana_atividade: [
                'terça-feira',
                'quarta-feira',
                'quinta-feira',
            ] 
        }
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. | 
| 204 | Sem conteúdo. |
| 400 | Má Requisição ou probida. |
| 404 | Não existe despesa com o id informado. |

---

## Cadastrar Dias

`POST` /api/x


**Campos de Requisição**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|:-------------:|-----------|
| data_id | int | Sim | O código do dia. | 
| data | Data | Sim | Uma data escolhida pelo usuário. |

**Exemplo de Corpo de Requisição**

```js
{
    data_id: 1,
    data: '2023-03-04',
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Despesa criada com sucesso. | 
| 400 | Os campos enviados são inválidos. |

---

## Detalhar Dias

`GET` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    data_id: 1,
    data: '2023-03-04',
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. |
| 204 | Sem conteúdo. |
| 404 | Não existe despesa com o id informado. |

---

## Atualizar Dias

`PATCH` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    data_id: 2,
    data: '2023-03-05',
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Dado atualizado com sucesso. | 
| 404 | Não existe despesa com o id informado. |
| 406 | Dado inválido ou errado. |

## Deletar Dias

`DELETE` /api/x/{id}

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dado deletado com sucesso. | 
| 404 | Não existe despesa com o id informado. |


## Detalhar Todos Dias

`GET` /api/x/

**Exemplo de Corpo de Requisição**

```js
{
    [
        {
            data_id: 1,
            data: '2023-03-04',
        },
        {
            data_id: 2,
            data: '2023-03-05',
        }
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. | 
| 204 | Sem conteúdo. |
| 400 | Má Requisição ou probida. |
| 404 | Não existe despesa com o id informado. |

---

## Cadastrar Semanas

`POST` /api/x


**Campos de Requisição**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|:-------------:|-----------|
| atividade_semana_id | int | Sim | O código da semana. |
| semana | Int | Sim | O número relativo ao dia da semana escolhido pelo usuário. |

**Exemplo de Corpo de Requisição**

```js
{
    atividade_semana_id: 1,
    semana: [
        1: 'segunda-feira',
        2: 'terça-feira',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Despesa criada com sucesso. | 
| 400 | Os campos enviados são inválidos. |

---

## Detalhar Semanas

`GET` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    atividade_semana_id: 1,
    semana: [
        1: 'segunda-feira',
        2: 'terça-feira',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. |
| 204 | Sem conteúdo. |
| 404 | Não existe despesa com o id informado. |

---

## Atualizar Semanas

`PATCH` /api/x/{id}

**Exemplo de Corpo de Requisição**

```js
{
    atividade_semana_id: 2,
    semana: [
        5: 'sexta-feira',
        6: 'sábado',
        7: 'domingo',
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 201 | Dado atualizado com sucesso. | 
| 404 | Não existe despesa com o id informado. |
| 406 | Dado inválido ou errado. |

## Deletar Semanas

`DELETE` /api/x/{id}

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dado deletado com sucesso. | 
| 404 | Não existe despesa com o id informado. |


## Detalhar Todas Semanas

`GET` /api/x/

**Exemplo de Corpo de Requisição**

```js
{
    [
        {
            atividade_semana_id: 1,
            semana: [
                1: 'segunda-feira',
                2: 'terça-feira',
            ],
        }
        {
            atividade_semana_id: 2,
            semana: [
                5: 'sexta-feira',
                6: 'sábado',
                7: 'domingo',
            ],
        }
    ]
}
```

**Código de Resposta**

| Código | Descrição |
| - | - |
| 200 | Dados retornados com sucesso. | 
| 204 | Sem conteúdo. |
| 400 | Má Requisição ou probida. |
| 404 | Não existe despesa com o id informado. |

---