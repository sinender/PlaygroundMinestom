package managers

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.BsonInt64
import org.bson.Document

var database: MongoDatabase? = null
suspend fun startupDatabase(
    mongoUri: String,
    databaseName: String
): MongoDatabase? {
    return try {
        val client = MongoClient.create(mongoUri)
        val db = client.getDatabase(databaseName)

        // Send a ping to confirm a successful connection
        val command = Document("ping", BsonInt64(1))
        db.runCommand(command)
        println("Pinged your deployment. You successfully connected to MongoDB!")
        database = db
        db
    } catch (e: Exception) {
        null
    }
}

suspend fun shutdown(database: MongoDatabase) {
    database.drop()
    println("Closed the MongoDB connection.")
}