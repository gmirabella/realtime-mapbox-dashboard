package com.project.dashboard

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException


@ResponseStatus(code = HttpStatus.NOT_FOUND)
class DownloadNotFoundException(message: String) : RuntimeException(message)