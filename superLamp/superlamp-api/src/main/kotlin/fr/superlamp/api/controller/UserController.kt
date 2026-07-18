package fr.superlamp.api.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): String {
        return "User endpoint for id: $id - To be implemented"
    }
}
