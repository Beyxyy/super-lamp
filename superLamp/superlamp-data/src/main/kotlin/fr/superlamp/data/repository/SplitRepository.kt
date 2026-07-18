package fr.superlamp.data.repository

import fr.superlamp.data.entity.Split
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SplitRepository : JpaRepository<Split, Long>
