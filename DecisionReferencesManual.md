# Руководство по реализации справочников "Органы, принимающие решения" и "Типы решений"

## Общее описание

В системе существуют два справочных типа данных, связанных с процессом принятия решений:
1. **DecisionMakingBody** (Орган, принимающий решение) - организация или подразделение, уполномоченное принимать решения
2. **DecisionType** (Тип решения) - категория или классификация принимаемых решений

## Назначение справочников

### DecisionMakingBody (Орган, принимающий решение)
- Используется для идентификации организации/подразделения, которое имеет право принимать решения по кредитным заявкам
- Примеры: "Кредитный комитет", "Департамент рисков", "Совет директоров"
- Связан с сущностью "Decision" (Решение) через поле `decisionMakingBody`

### DecisionType (Тип решения)
- Используется для классификации решений по их типу
- Примеры: "Одобрение кредита", "Отказ в кредите", "Условное одобрение"
- Связан с сущностью "Decision" (Решение) через поле `decisionType`

## Структура сущностей

Обе сущности наследуются от `BaseMultilingualReferenceEntity`, которая включает:
- `id` - уникальный идентификатор
- `nameEn` - название на английском языке
- `nameRu` - название на русском языке
- `nameKg` - название на кыргызском языке
- `description` - описание (необязательное поле)
- `status` - статус (ACTIVE/INACTIVE)
- `createdAt`, `updatedAt` - даты создания и обновления

## REST API Endpoints

### DecisionMakingBody

#### Получение данных
- `GET /api/decision-making-bodies` - получить список всех органов, принимающих решения (с пагинацией)
- `GET /api/decision-making-bodies/{id}` - получить орган по ID
- `GET /api/decision-making-bodies/active` - получить список активных органов (с пагинацией)
- `GET /api/decision-making-bodies/search?searchTerm={term}` - поиск по названию или описанию

#### Создание и изменение
- `POST /api/decision-making-bodies` - создать новый орган
- `PUT /api/decision-making-bodies/{id}` - обновить существующий орган
- `DELETE /api/decision-making-bodies/{id}` - удалить орган (только если не используется в решениях)

#### Вспомогательные
- `GET /api/decision-making-bodies/exists/name-ru/{nameRu}` - проверить существование органа с таким названием на русском

### DecisionType

#### Получение данных
- `GET /api/decision-types` - получить список всех типов решений (с пагинацией)
- `GET /api/decision-types/{id}` - получить тип решения по ID
- `GET /api/decision-types/active` - получить список активных типов решений (с пагинацией)
- `GET /api/decision-types/search?searchTerm={term}` - поиск по названию или описанию

#### Создание и изменение
- `POST /api/decision-types` - создать новый тип решения
- `PUT /api/decision-types/{id}` - обновить существующий тип решения
- `DELETE /api/decision-types/{id}` - удалить тип решения (только если не используется в решениях)

#### Вспомогательные
- `GET /api/decision-types/exists/name-ru/{nameRu}` - проверить существование типа решения с таким названием на русском

## DTO (Data Transfer Objects)

### Request DTOs

#### DecisionMakingBodyCreateRequest
```java
String nameEn;     // Название на английском (обязательное)
String nameRu;     // Название на русском (обязательное)
String nameKg;     // Название на кыргызском (обязательное)
String description; // Описание (необязательное)
ReferenceEntityStatus status; // Статус (обязательное)
```

#### DecisionMakingBodyUpdateRequest
```java
String nameEn;     // Название на английском (необязательное)
String nameRu;     // Название на русском (необязательное)
String nameKg;     // Название на кыргызском (необязательное)
String description; // Описание (необязательное)
ReferenceEntityStatus status; // Статус (необязательное)
```

#### DecisionTypeCreateRequest
```java
String nameEn;     // Название на английском (обязательное)
String nameRu;     // Название на русском (обязательное)
String nameKg;     // Название на кыргызском (обязательное)
String description; // Описание (необязательное)
ReferenceEntityStatus status; // Статус (обязательное)
```

#### DecisionTypeUpdateRequest
```java
String nameEn;     // Название на английском (необязательное)
String nameRu;     // Название на русском (необязательное)
String nameKg;     // Название на кыргызском (необязательное)
String description; // Описание (необязательное)
ReferenceEntityStatus status; // Статус (необязательное)
```

### Response DTOs

#### DecisionMakingBodyResponse
```java
Long id;
String nameEn;
String nameRu;
String nameKg;
String description;
ReferenceEntityStatus status;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### DecisionTypeResponse
```java
Long id;
String nameEn;
String nameRu;
String nameKg;
String description;
ReferenceEntityStatus status;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

## Бизнес-логика

1. **Уникальность**: Проверяется уникальность `nameRu` при создании и обновлении
2. **Проверка ссылок**: Удаление возможно только если запись не используется в решениях
3. **Статусы**: Поддерживаются активные и неактивные записи
4. **Мультиязычность**: Все названия хранятся на трех языках

## Репозитории

Оба репозитория наследуются от `JpaRepository` и `JpaSpecificationExecutor` и содержат:
- Методы поиска по `nameRu`
- Методы проверки существования по `nameRu`
- Методы получения записей по статусу
- Методы проверки использования в решениях

## Сервисы

Сервисы реализуют CRUD операции с соответствующей валидацией:
- Создание с проверкой уникальности
- Обновление с проверкой уникальности (если меняется nameRu)
- Удаление с проверкой использования в решениях
- Поиск и пагинация