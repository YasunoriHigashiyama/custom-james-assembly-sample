package jp.co.neosystem.wg;

import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.james.*;
import org.apache.james.data.UsersRepositoryModuleChooser;
import org.apache.james.modules.data.MemoryUsersRepositoryModule;
import org.apache.james.modules.protocols.ProtocolHandlerModule;
import org.apache.james.modules.protocols.SMTPServerModule;
import org.apache.james.modules.server.MailetContainerModule;
import org.apache.james.modules.server.RawPostDequeueDecoratorModule;

public class CustomJamesServerMain implements JamesServerMain {
	public static final Module SMTP_ONLY_MODULE = Modules.combine(
			MemoryJamesServerMain.IN_MEMORY_SERVER_MODULE,
			new ProtocolHandlerModule(),
			new SMTPServerModule(),
			new RawPostDequeueDecoratorModule(),
			binder -> binder.bind(MailetContainerModule.DefaultProcessorsConfigurationSupplier.class)
					.toInstance(BaseHierarchicalConfiguration::new));

	public static void main(String[] args) throws Exception {
		ExtraProperties.initialize();

		MemoryJamesConfiguration configuration = MemoryJamesConfiguration.builder()
				.useWorkingDirectoryEnvProperty()
				.build();

		LOGGER.info("Loading configuration {}", configuration.toString());
		GuiceJamesServer server = createServer(configuration)
				.combineWith(new FakeSearchMailboxModule());

		JamesServerMain.main(server);
		return;
	}

	public static GuiceJamesServer createServer(MemoryJamesConfiguration configuration) {
		return GuiceJamesServer.forConfiguration(configuration)
				.combineWith(SMTP_ONLY_MODULE)
				.combineWith(new UsersRepositoryModuleChooser(new MemoryUsersRepositoryModule())
						.chooseModules(configuration.getUsersRepositoryImplementation()));
	}
}