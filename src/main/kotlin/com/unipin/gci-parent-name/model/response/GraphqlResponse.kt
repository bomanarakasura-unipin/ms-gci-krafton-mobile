package com.unipin.gci-parent-name.model.response

data class GraphqlResponse<T>(
    val data: T? = null,
    val errors: List<String> = emptyList(),
)
