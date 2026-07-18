package fr.superlamp.api.config

import fr.superlamp.core.service.ExerciseCoreService
import fr.superlamp.core.service.LiftCoreService
import fr.superlamp.core.service.SetCoreService
import fr.superlamp.core.service.SplitCoreService
import fr.superlamp.core.service.WorkoutCoreService
import fr.superlamp.core.service.WorkoutExerciseCoreService
import fr.superlamp.data.adapter.ExerciseRepositoryAdapter
import fr.superlamp.data.adapter.LiftRepositoryAdapter
import fr.superlamp.data.adapter.SetRepositoryAdapter
import fr.superlamp.data.adapter.SplitRepositoryAdapter
import fr.superlamp.data.adapter.WorkoutExerciseRepositoryAdapter
import fr.superlamp.data.adapter.WorkoutRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Configuration Spring pour créer les beans des services Core
 * Les services Core ne sont pas des @Service car ils sont dans un module sans Spring
 * On les crée ici en injectant les adapters (qui sont des @Component dans data)
 */
@Configuration
@ComponentScan(basePackages = ["fr.superlamp.data.adapter", "fr.superlamp.data.repository"])
class CoreConfig {
    
    @Bean
    fun workoutCoreService(workoutRepositoryAdapter: WorkoutRepositoryAdapter): WorkoutCoreService {
        return WorkoutCoreService(workoutRepositoryAdapter)
    }
    
    @Bean
    fun exerciseCoreService(exerciseRepositoryAdapter: ExerciseRepositoryAdapter): ExerciseCoreService {
        return ExerciseCoreService(exerciseRepositoryAdapter)
    }
    
    @Bean
    fun splitCoreService(splitRepositoryAdapter: SplitRepositoryAdapter): SplitCoreService {
        return SplitCoreService(splitRepositoryAdapter)
    }
    
    @Bean
    fun liftCoreService(liftRepositoryAdapter: LiftRepositoryAdapter): LiftCoreService {
        return LiftCoreService(liftRepositoryAdapter)
    }
    
    @Bean
    fun workoutExerciseCoreService(
        workoutExerciseRepositoryAdapter: WorkoutExerciseRepositoryAdapter
    ): WorkoutExerciseCoreService {
        return WorkoutExerciseCoreService(workoutExerciseRepositoryAdapter)
    }
    
    @Bean
    fun setCoreService(setRepositoryAdapter: SetRepositoryAdapter): SetCoreService {
        return SetCoreService(setRepositoryAdapter)
    }
}
