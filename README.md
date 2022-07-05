# poc custom observability headers

### use observability headers trace-id and span-id

### example:

```
curl --location --request GET 'http://localhost:8080/observability' \
--header 'trace-id: 123' \
--header 'span-id: 456'
```