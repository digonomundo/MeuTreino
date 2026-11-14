# Guia de Contribuição

Obrigado pelo interesse em contribuir com o MeuTreino. O projeto é um aplicativo Android em Java para organizar treinos de musculação. Relatos de bugs, sugestões e melhorias são bem-vindos, desde que estejam alinhados ao escopo atual do app.

Ao participar, siga o [Código de Conduta](./CODE_OF_CONDUCT.md). Para vulnerabilidades, consulte a [Política de Segurança](./SECURITY.md) e não abra uma issue pública com detalhes exploráveis.

## Antes de começar

- Para mudanças pequenas, abra uma issue descrevendo o problema ou a proposta.
- Para funcionalidades maiores ou que alterem o comportamento do app, converse sobre o escopo antes de iniciar o trabalho.
- Consulte o README para conhecer as [funcionalidades, a arquitetura e as limitações atuais](./README.md).

## Preparar o ambiente

O projeto usa Java 8 para o código Android, Gradle Wrapper 8.13, Android Gradle Plugin 8.13.2, `compileSdk` 33 e `minSdk` 21. É necessário usar uma versão do JDK compatível com o Android Gradle Plugin; o JDK 17 do Android Studio é recomendado para executar o Gradle.

1. Abra ou clone o repositório no Android Studio.
2. Configure `local.properties` com o caminho do Android SDK.
3. Configure `app/google-services.json` para um projeto Firebase de desenvolvimento compatível com `com.example.meutreino`.
4. Sincronize o Gradle antes de compilar.

Os arquivos de configuração local são ignorados pelo Git. Não adicione credenciais, chaves privadas, dados de usuários ou configurações locais ao commit. Use um projeto Firebase e contas de teste; não execute testes contra dados reais de usuários.

## Implementar uma mudança

- Mantenha as alterações focadas e siga os padrões existentes de Java, XML, View Binding e organização próxima de MVVM.
- Preserve os identificadores e `firestoreKey` já usados pelos exercícios, salvo se houver uma migração compatível. Eles podem corresponder a preferências já salvas no Firestore.
- Mantenha as limitações do app claras: não descreva funcionalidades não implementadas como existentes nem associe imagens ou vídeos a exercícios incompatíveis.
- Atualize a documentação quando o comportamento, a configuração ou uma limitação descrita mudar.
- Evite incluir logs, capturas de tela ou exemplos que exponham dados pessoais, credenciais ou identificadores de contas reais.

## Validar as mudanças

Execute as verificações disponíveis antes de abrir um pull request:

```bash
bash gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
```

`assembleDebugAndroidTest` compila e empacota os testes instrumentados, mas não os executa. Para executá-los, é necessário um emulador ou dispositivo conectado:

```bash
bash gradlew :app:connectedDebugAndroidTest
```

Se uma verificação não puder ser executada, informe qual e por quê no pull request. Não apresente uma compilação ou teste não executado como aprovado.

## Commits e pull requests

Use commits pequenos, com mensagens que descrevam a intenção da mudança. O projeto adota prefixos semânticos, por exemplo:

- `feat: add exercise instructions`
- `fix: handle incomplete user profile`
- `docs: update setup instructions`
- `test: cover workout model behavior`
- `refactor: simplify profile state handling`
- `build: update Android configuration`

Para propor mudanças:

1. Crie uma branch a partir de `main`.
2. Faça commits focados e execute as verificações relevantes.
3. Abra um pull request para `main`, com resumo do problema, da solução e dos testes executados.
4. Inclua passos para reproduzir mudanças visuais ou comportamentais e imagens sem dados pessoais, quando forem úteis.

Issues e pull requests devem ser objetivos, respeitosos e focados no projeto. Para dúvidas ou relatos comuns, use as [issues do repositório](https://github.com/digonomundo/MeuTreino/issues).
