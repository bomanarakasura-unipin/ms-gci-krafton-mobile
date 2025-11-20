package com.unipin.gci-parent-name.model.response

data class CredentialResponse(
    val secretMsCredentials: SecretMsCredentials? = null,
) {
    data class SecretMsCredentials(
        val data: List<Credential> = emptyList(),
    )

    data class Credential(
        val guid: String,
        val code: String,
        val credentialDetail: List<CredentialDetail> = emptyList(),
    )

    data class CredentialDetail(
        val guid: String,
        val name: String,
        val value: String,
    )
}
