package fr.superlamp.api.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController {

        // private val userCoreService: UserCoreService
    
    
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): String {
        return "User endpoint for id: $id - To be implemented"
    }
    
    @GetMapping("")
    fun getAllUsers() : String {
        return "User endpoint - To be implemented"
    }
    
    // @PostMapping("")
    // fun createUser(@RequestBody request: UserRequest) : UserResponse {
    //     // 
    // }
}
