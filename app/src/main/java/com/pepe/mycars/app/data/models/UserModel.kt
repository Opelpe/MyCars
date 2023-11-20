package com.pepe.mycars.app.data.models

class UserModel(
    private var email: String,
    private var password: String,
    private var nick: String,
    private var name: String,
    private var surName: String,
    private var id: Long,
    private var isLogged: Boolean = false
) {

    fun setEmail(email: String) {
        this.email = email
    }

    fun getEmail(): String {
        return email
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getPassword(): String {
        return password
    }

    fun setNick(nick: String) {
        this.nick = nick
    }

    fun getNick(): String {
        return nick
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun setSurName(surName: String) {
        this.surName = surName
    }

    fun getSurName(): String {
        return surName
    }

    fun setId(userId: Long) {
        this.id = userId
    }

    fun getId(): Long {
        return id
    }

    fun isLogged(): Boolean {
        return isLogged
    }

    fun setUserLogged(isLogged: Boolean) {
        this.isLogged = isLogged
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserModel

        if (email != other.email) return false
        if (password != other.password) return false
        if (nick != other.nick) return false
        if (name != other.name) return false
        if (surName != other.surName) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + nick.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surName.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}