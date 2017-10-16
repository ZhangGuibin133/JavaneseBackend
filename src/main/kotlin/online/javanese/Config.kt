package online.javanese

import java.util.*

class Config(
        props: Properties
) {

    val dbName: String = props.getProperty("database")
    val dbUser: String = props.getProperty("user")
    val dbPassword: String = props.getProperty("password")

    // /home/<user>/IdeaProjects/javanese/src/main/resources/static
    val localStaticDir: String? = props.getProperty("localStaticDir")

    // /path/to/static/files/whatever
    val exposedStaticDir: String = props.getProperty("exposedStaticDir")

    // http://javanese.online
    val siteUrl = props.getProperty("siteUrl")

}