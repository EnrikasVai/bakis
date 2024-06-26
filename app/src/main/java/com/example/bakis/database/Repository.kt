package com.example.bakis.database

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
Clean architecture pattern
good for separation of logic
 */


/* creating an interface helps in abstraction and easily we can add or remove
required methods without a hassle.
*/
interface Repository {

    suspend fun insert(userEntity: UserEntity)

    suspend fun delete(userEntity: UserEntity)

    suspend fun update(userEntity: UserEntity)

    suspend fun getAllUsers(): Flow<List<UserEntity>>

    suspend fun deleteAllUsers()

    //USER UPDATE PROFILE INFO
    suspend fun updateUserName(id: Int, newName: String)
    suspend fun updateUserAge(id: Int, newAge: Int)
    suspend fun updateUserWeight(id: Int, newWeight: Double)
    suspend fun updateUserHeight(id: Int, newHeight: Int)
    suspend fun updateUserSex(id: Int, newSex: Boolean)
    suspend fun updateUserWaterGoal(id: Int, newWaterGoal: Int)
    suspend fun updateUserStepGoal(id: Int, newStepGoal: Int)

        suspend fun insertWaterIntake(waterIntakeEntity: WaterIntakeEntity)
        fun getAllWaterIntakesForUser(userId: Int): Flow<List<WaterIntakeEntity>>
        fun getWaterIntakeForUserByDate(userId: Int, date: String): Flow<List<WaterIntakeEntity>>
        suspend fun deleteWaterIntake(waterIntakeEntity: WaterIntakeEntity)


}

class RepositoryImpl @Inject constructor(
        private val dao: MyDao,
        ) : Repository {
        override suspend fun insert(userEntity: UserEntity) {
        withContext(IO) {
        dao.insert(userEntity)
        }
        }

        override suspend fun delete(userEntity: UserEntity) {
        withContext(IO) {
        dao.delete(userEntity)
        }
        }

        override suspend fun update(userEntity: UserEntity) {
        withContext(IO) {
        dao.update(userEntity)
        }
        }

        override suspend fun getAllUsers(): Flow<List<UserEntity>> {
        return withContext(IO) {
        dao.getAllUsers()
        }
        }
        override suspend fun deleteAllUsers() {
                withContext(IO) {
                        dao.deleteAllUsers()
                }
        }
        //Update Info profile

        override suspend fun updateUserName(id: Int, newName: String) {
                withContext(IO) {
                        dao.updateUserName(id, newName)
                }
        }

        override suspend fun updateUserAge(id: Int, newAge: Int) {
                withContext(IO) {
                        dao.updateUserAge(id, newAge)
                }
        }
        override suspend fun updateUserWeight(id: Int, newWeight: Double) {
                withContext(IO) {
                        dao.updateUserWeight(id, newWeight)
                }
        }

        override suspend fun updateUserHeight(id: Int, newHeight: Int) {
                withContext(IO) {
                        dao.updateUserHeight(id, newHeight)
                }
        }
        override suspend fun updateUserSex(id: Int, newSex: Boolean) {
                withContext(IO) {
                        dao.updateUserSex(id, newSex)
                }
        }
        override suspend fun updateUserWaterGoal(id: Int, newWaterGoal: Int) {
                withContext(IO) {
                        dao.updateUserWaterGoal(id, newWaterGoal)
                }
        }
        override suspend fun updateUserStepGoal(id: Int, newStepGoal: Int) {
                withContext(IO) {
                        dao.updateUserStepGoal(id, newStepGoal)
                }
        }
        override suspend fun insertWaterIntake(waterIntakeEntity: WaterIntakeEntity) {
                withContext(IO) {
                        dao.insertWaterIntake(waterIntakeEntity)
                }
        }

        override fun getAllWaterIntakesForUser(userId: Int): Flow<List<WaterIntakeEntity>> = dao.getAllWaterIntakesForUser(userId)

        override fun getWaterIntakeForUserByDate(userId: Int, date: String): Flow<List<WaterIntakeEntity>> = dao.getWaterIntakeForUserByDate(userId, date)

        override suspend fun deleteWaterIntake(waterIntakeEntity: WaterIntakeEntity) {
                withContext(IO) {
                        dao.deleteWaterIntake(waterIntakeEntity)
                }
        }

}

