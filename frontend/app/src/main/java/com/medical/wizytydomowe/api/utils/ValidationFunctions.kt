package com.medical.wizytydomowe.api.utils

import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.R


fun validatePersonalDataFields(firstName: String?, lastName: String?,
                                   firstNameLayout: TextInputLayout?, lastNameLayout: TextInputLayout?): Boolean{
    firstNameLayout?.error = null
    lastNameLayout?.error = null

    when{
        firstName.isNullOrEmpty() -> {
            firstNameLayout?.error = "Pole 'Imię' jest wymagane"
            return false
        }
        lastName.isNullOrEmpty() -> {
            lastNameLayout?.error = "Pole 'Nazwisko' jest wymagane"
            return false
        }
    }
    return true
}

fun validateContactFields(email: String?, phoneNumber: String?, emailLayout: TextInputLayout?,
                              phoneNumberLayout: TextInputLayout?): Boolean{
    emailLayout?.error = null
    phoneNumberLayout?.error = null

    when{
        email.isNullOrEmpty() -> {
            emailLayout?.error = "Pole 'E-mail' jest wymagane"
            return false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            emailLayout?.error = "Nieprawidłowy format pola 'E-mail'"
            return false
        }
        phoneNumber.isNullOrEmpty() -> {
            phoneNumberLayout?.error = "Pole 'Numer telefonu' jest wymagane"
            return false
        }
        !phoneNumber.matches("^\\d{9}\$".toRegex()) -> {
            phoneNumberLayout?.error = "Nieprawidłowy format pola 'Numer telefonu'"
            return false
        }
    }
    return true
}

fun validateDateOfBirth(dateOfBirth: String?, dateOfBirthLayout: TextInputLayout?): Boolean{
    dateOfBirthLayout?.error = null

    when{
        dateOfBirth.isNullOrEmpty() -> {
            dateOfBirthLayout?.error = "Pole 'Data urodzenia' jest wymagane"
            return false
        }
        !dateOfBirth.matches("^\\d{2}-\\d{2}-\\d{4}\$".toRegex()) -> {
            dateOfBirthLayout?.error = "Nieprawidłowy format pola 'Data urodzenia'"
            return false
        }
        !isValidDate(dateOfBirth) -> {
            dateOfBirthLayout?.error = "Nieprawidłowy format pola 'Data urodzenia'"
            return false
        }
    }
    return true
}

fun validateAddress(city: String?, street: String?, buildingNumber: String?, postalCode: String?,
                    cityLayout: TextInputLayout?, streetLayout: TextInputLayout?,
                    postalCodeLayout: TextInputLayout?, buildingNumberLayout: TextInputLayout?): Boolean{
    cityLayout?.error = null
    streetLayout?.error = null
    postalCodeLayout?.error = null
    buildingNumberLayout?.error = null

    when{
        city.isNullOrEmpty() -> {
            cityLayout?.error = "Pole 'Miasto' jest wymagane"
            return false
        }
        street.isNullOrEmpty() -> {
            streetLayout?.error = "Pole 'Ulica' jest wymagane"
            return false
        }
        buildingNumber.isNullOrEmpty() -> {
            buildingNumberLayout?.error = "Pole 'Numer budynku' jest wymagane"
            return false
        }
        postalCode.isNullOrEmpty() -> {
            postalCodeLayout?.error = "Pole 'Kod pocztowy' jest wymagane"
            return false
        }
        !postalCode.matches("^\\d{2}-\\d{3}\$".toRegex()) -> {
            postalCodeLayout?.error = "Nieprawidłowy format pola 'Kod pocztowy'"
            return false
        }
    }
    return true
}

fun validateLoginInputs(email: String, password: String, emailLayout: TextInputLayout?,
                        passwordLayout: TextInputLayout?): Boolean {
    emailLayout?.error = null
    passwordLayout?.error = null

    when {
        email.isEmpty() -> {
            emailLayout?.error = "Pole 'E-mail' jest wymagane"
            return false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            emailLayout?.error = "Nieprawidłowy format pola 'E-mail'"
            return false
        }
        password.isEmpty() -> {
            passwordLayout?.error = "Pole 'Hasło' jest wymagane"
            return false
        }
    }
    return true
}

fun validateDescription(description: String?, descriptionLayout: TextInputLayout?): Boolean{
    descriptionLayout?.error = null

    when{
        description.isNullOrEmpty() -> {
            descriptionLayout?.error = "Pole 'Opis' nie powinno być puste"
            return false
        }
    }
    return true
}

fun validateFilterText(filterText: String?, filterTextLayout: TextInputLayout?): Boolean{
    filterTextLayout?.error = null

    when{
        filterText.isNullOrEmpty() -> {
            filterTextLayout?.error = "Pole 'Filtracja' nie powinno być puste"
            return false
        }
    }
    return true
}

fun validateOldPassword(password: String?, passwordLayout: TextInputLayout?): Boolean{
    passwordLayout?.error = null

    when{
        password.isNullOrEmpty() -> {
            passwordLayout?.error = "Pole 'Hasło' jest wymagane"
            return false
        }
        password.length < 6 -> {
            passwordLayout?.error = "Pole 'Hasło' powinno zawierać min. 6 znaków"
            return false
        }
    }
    return true
}

fun validateNewPassword(password: String?, passwordConfirmation : String?, passwordLayout: TextInputLayout?,
                        passwordConfirmationLayout: TextInputLayout?): Boolean{
    passwordLayout?.error = null
    passwordConfirmationLayout?.error = null

    when{
        password.isNullOrEmpty() -> {
            passwordLayout?.error = "Pole 'Hasło' jest wymagane"
            return false
        }
        password.length < 6 -> {
            passwordLayout?.error = "Pole 'Hasło' powinno zawierać min. 6 znaków"
            return false
        }
        passwordConfirmation.isNullOrEmpty() -> {
            passwordConfirmationLayout?.error = "Pole 'Potwierdzenie hasła' jest wymagane"
            return false
        }
        !password.equals(passwordConfirmation) -> {
            passwordConfirmationLayout?.error = "Wprowadzone hasła nie są jednakowe"
            return false
        }
    }
    return true
}

fun validateNewMedication(name: String?, dosage : String?, nameLayout: TextInputLayout?,
                          dosageLayout: TextInputLayout?): Boolean{
    nameLayout?.error = null
    dosageLayout?.error = null

    when{
        name.isNullOrEmpty() -> {
            nameLayout?.error = "Pole 'Nazwa lekarstwa' jest wymagana"
            return false
        }
        dosage.isNullOrEmpty() -> {
            dosageLayout?.error = "Pole 'Dawkowanie' jest wymagane"
            return false
        }
    }
    return true
}