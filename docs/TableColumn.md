Examples:

```kt
TableColumn(
    name = "Headline", weight = 1f,
    onClickCell = { nav.go(SourceItemRoute(it.source.id)) }
) { TextCell(it.source.title) }
```
