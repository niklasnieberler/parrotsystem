import net.mrmanhd.parrot.api.ParrotApi
import net.mrmanhd.parrot.api.group.IParrotGroup

class ProgressTest {

    init {
        ParrotApi.instance.getServiceHandler().createProgress(ParrotGroupDummy())
            .maxPlayers(200)
            .motd("Hallo ich bin ein Text")
            .property("test", "123")
            .startService()
                .addResultListener { println("Starting") }
                .addFailureListener { println(it.message) }
    }

}

class ParrotGroupDummy : IParrotGroup {
    override fun getName(): String {
        TODO("Not yet implemented")
    }
}