# MeuTreino

Documentação unificada do aplicativo MeuTreino. Este documento reúne a visão geral, as instruções de uso e desenvolvimento, a análise técnica e o contexto acadêmico do projeto. Ele descreve a implementação atual e distingue as funcionalidades existentes das propostas originais que não fazem parte do aplicativo.

## Índice

- [Visão geral](#visão-geral)
- [Contexto e objetivos](#contexto-e-objetivos)
- [Escopo atual](#escopo-atual)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Persistência e dados](#persistência-e-dados)
- [Tecnologias e requisitos](#tecnologias-e-requisitos)
- [Configuração do ambiente](#configuração-do-ambiente)
- [Build e testes](#build-e-testes)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Limitações e cuidados](#limitações-e-cuidados)
- [Documentação complementar](#documentação-complementar)
- [Metodologia e validação acadêmica](#metodologia-e-validação-acadêmica)

## Visão geral

**MeuTreino** é um aplicativo Android para organizar treinos de musculação. A pessoa usuária pode criar uma conta, preencher ou editar seu perfil, escolher exercícios por grupo muscular, configurar séries e acompanhar uma sessão de treino. O app também oferece instruções e demonstrações em vídeo para os exercícios associados a recursos disponíveis.

O aplicativo é voltado ao uso individual por praticantes. Seu objetivo é facilitar o acesso às informações do treino durante a prática, sem substituir a avaliação de um profissional de Educação Física nem acompanhamento médico.

O projeto foi desenvolvido no contexto da disciplina de *Programação para Dispositivos Móveis* do curso de *Engenharia de Telecomunicações* da **Faculdade de Tecnologia da Universidade Estaudual de Campinas (FT - UNICAMP)**, em Limeira (SP), com autoria de [**Rodrigo Marques Dias**](https://www.digonomundo.com/pt), RA: 184789. 

## Contexto e objetivos

Atividade física e musculação são praticadas por pessoas com objetivos e condições diferentes. Um recurso móvel pode manter instruções de exercícios e informações do treino acessíveis, apoiar a organização da sessão e reduzir o atrito de consultar anotações separadas. A ideia do MeuTreino surgiu nesse contexto de uso cotidiano de smartphones, ferramentas digitais e acompanhamento de atividades físicas.

O trabalho propõe um aplicativo móvel para apoiar usuários de academia com treinos personalizados, acompanhamento de progresso e orientações de exercícios. As palavras-chave registradas são: **aplicativo móvel, academia, treinamento personalizado, acompanhamento de progresso e exercícios**. 

O contexto de origem discute o uso da tecnologia em qualidade de vida, medicina, educação, comunicação e inovação, além de sistemas de informação e aplicativos fitness em academias. Como ideia geral do app, queria tratar de objetivos como redução de gordura corporal, ganho de massa muscular, disposição e qualidade de vida. Hoje, academias discutem o uso de sistemas para acompanhar alunos e a pressão adicional que controles mais precisos podem trazer ao trabalho dos instrutores. 

### Objetivos do aplicativo

- Oferecer um fluxo simples de autenticação e perfil.
- Reunir um catálogo de exercícios dividido por grupos musculares.
- Permitir a montagem de uma lista de exercícios e a configuração do número de séries.
- Apresentar instruções e demonstrações quando houver recurso compatível.
- Apoiar o registro de séries, carga, cronômetro e descanso durante a sessão.
- Manter dados de conta e preferências disponíveis entre sessões usando Firebase.

## Escopo atual

O aplicativo implementa principalmente os fluxos de uso individual: autenticação, perfil, catálogo, montagem e acompanhamento de uma sessão. Não há painel de instrutor, gestão de academia, gestão de alunos, rede social ou comunidade. Também ainda não há relatório de evolução corporal nem histórico persistido de sessões.

## Funcionalidades

### Inicialização e autenticação

- `SplashActivity` verifica se há uma sessão autenticada e direciona para a área principal ou para o login.
- Login e criação de conta usam Firebase Authentication com e-mail e senha.
- O cadastro exige uma senha com pelo menos seis caracteres.
- A recuperação de senha solicita o envio de uma mensagem pelo Firebase.
- Após criar a conta, a pessoa pode preencher os dados do perfil ou continuar sem preencher e editá-los posteriormente.

### Home e navegação

- A Home mostra uma saudação de “Bom dia”, “Boa tarde” ou “Boa noite”, conforme o horário local.
- Quando há nome cadastrado, a saudação usa o primeiro nome e pergunta o que a pessoa gostaria de treinar.
- A tela oferece acesso às categorias de peito, abdômen, pernas, braços, costas e ombros.
- O menu lateral contém início, perfil e segurança da conta.
- A navegação principal é definida em `app/src/main/res/navigation/mobile_navigation.xml` e hospedada por `MainActivity`.

### Perfil e estatísticas

A pessoa pode informar ou editar nome, peso, altura e data de nascimento. O seletor de nascimento utiliza um diálogo com confirmação explícita e não permite datas futuras. O perfil incompleto pode ser preenchido posteriormente.

- A idade é calculada a partir da data de nascimento. O valor legado `idade` também é lido como alternativa quando não há data válida.
- O IMC é calculado como peso em quilogramas dividido pela altura em metros ao quadrado e recebe uma classificação textual.
- A estimativa de consumo de água é calculada como 35 ml por quilograma de peso corporal.
- Quando faltam dados, a tela informa que o perfil está incompleto e não apresenta um IMC calculado sem peso e altura válidos.

IMC e hidratação são estimativas informativas. A fórmula de hidratação não considera atividade, clima, condição de saúde ou recomendação individual.

### Catálogo e treino

O catálogo local possui **45 exercícios**:

| Grupo muscular | Quantidade |
| --- | ---: |
| Peito | 12 |
| Abdômen | 9 |
| Pernas | 6 |
| Braços | 6 |
| Costas | 6 |
| Ombros | 6 |
| **Total** | **45** |

Cada exercício possui identificador, chave de persistência, título, modalidade, categoria, imagem opcional, chave de vídeo e instruções.

Na montagem do treino, é possível selecionar ou desmarcar exercícios e configurar de 1 a 20 séries por exercício. O padrão é três séries. Durante a sessão, o app permite:

- registrar a conclusão de cada série;
- inserir carga em quilogramas;
- iniciar e pausar um cronômetro crescente por exercício;
- acompanhar um descanso regressivo após concluir uma série, com 60 segundos como padrão;
- concluir manualmente um exercício, mesmo que ainda haja séries não registradas;
- abrir as instruções e a demonstração disponível.

`WorkoutCategoryFragment` apresenta exercícios selecionados de uma categoria e oferece acesso à demonstração. `WorkoutSetupFragment` coordena seleção e configuração de séries. `WorkoutAdapter` apresenta imagens, carga, séries e controles usados na montagem e execução.

### Imagens e vídeos

As imagens do catálogo são opcionais. Para abdômen, cinco imagens disponíveis foram associadas a elevação de pernas no banco declinado, crunch declinado, abdominal na bola, abdominal no banco declinado e abdominal na polia. Uma imagem originalmente nomeada como oblíquo não evidencia rotação; por isso, a descrição correspondente foi ajustada para crunch declinado. Exercícios sem imagem compatível permanecem sem imagem.

Os vídeos são recursos locais em `app/src/main/res/raw`. `VideoPlayerFragment` exibe instruções e reproduz os recursos mapeados com Media3/ExoPlayer. Atualmente, o código associa as chaves `video1` a `video12` e `video34`. Para outra chave, ou quando o player não consegue carregar o recurso, a tela informa que o vídeo está indisponível. A existência de um arquivo MP4 em `res/raw` não significa que esteja associado a um exercício: a chave também precisa estar mapeada no player.

### Segurança da conta

A tela **E-mail e segurança** permite:

- solicitar a troca do e-mail, com envio de um link de confirmação pelo Firebase;
- alterar a senha após validar a senha atual e confirmar a nova senha;
- excluir a conta após confirmação explícita e reautenticação.

As operações sensíveis usam reautenticação por credencial de e-mail e senha. Na exclusão, o app remove primeiro o documento `usuarios/{uid}` e depois a conta Firebase Authentication. Se a segunda etapa falhar, a conta poderá permanecer ativa sem o perfil no Firestore; a tela apresenta o erro.

## Arquitetura

O projeto segue uma organização em camadas próxima de **MVVM**, sem uma camada de domínio separada:

- **Apresentação (`ui/`)**: Activities e Fragments exibem as telas e delegam ações aos ViewModels.
- **Estado de tela**: ViewModels expõem estados por LiveData e validam alguns dados antes de chamar repositórios.
- **Modelos (`data/model/`)**: definem `User`, `Exercise`, `WorkoutCategory` e `ExerciseCatalog`.
- **Repositórios (`data/repository/`)**: encapsulam operações de Firebase Authentication e Cloud Firestore.
- **Recursos (`res/`)**: layouts, tema, ícones, imagens, grafo de navegação, menus e arquivos de vídeo.

Fluxo de dados mais comum:

```text
Activity/Fragment → ViewModel → Repository → Firebase
                         ↑                     │
                         └──── LiveData ───────┘
```

O catálogo de exercícios é local. O Firestore mantém dados de perfil e preferências do usuário. Imagens e vídeos usados pelo app são empacotados como recursos Android.

### Principais componentes

| Componente | Responsabilidade |
| --- | --- |
| `SplashActivity` | Direcionamento inicial conforme sessão autenticada |
| `LoginActivity` | Entrada na conta |
| `RegisterActivity` | Criação da conta e início do preenchimento opcional do perfil |
| `PasswordResetActivity` | Solicitação de recuperação de senha |
| `UserDataInputActivity` | Preenchimento ou edição do perfil e seleção da data de nascimento |
| `MainActivity` | Menu lateral e host de navegação |
| `HomeFragment` | Saudação e acesso às seis categorias |
| `UserProfileFragment` | Dados, estatísticas, edição, segurança e saída |
| `AccountSecurityFragment` | Troca de e-mail ou senha e exclusão da conta |
| `WorkoutCategoryFragment` | Exercícios selecionados de uma categoria e acesso ao vídeo |
| `WorkoutSetupFragment` | Seleção de exercícios e quantidade de séries |
| `VideoPlayerFragment` | Instruções e reprodução local |
| `WorkoutAdapter` | Apresentação dos exercícios e controles de montagem e execução |

### Componentes de dados

- `User`: nome, peso, altura, idade, nascimento e cálculos de saúde.
- `Exercise`: metadados do exercício, séries, carga e estado da sessão.
- `WorkoutCategory`: categorias e respectivas chaves persistidas.
- `ExerciseCatalog`: fonte local e consultas por categoria ou chave de vídeo.
- `AuthRepository`: operações de Firebase Authentication.
- `UserRepository`: leitura e gravação do perfil e da chave de vídeo escolhida.
- `WorkoutRepository`: leitura do catálogo com preferências e gravação de seleção e séries.
- `AccountSecurityRepository`: reautenticação, alteração de e-mail ou senha e exclusão.

## Persistência e dados

O documento principal de cada usuário fica no Firestore em:

```text
usuarios/{uid}
```

Campos de perfil gravados pelo app:

| Campo | Conteúdo |
| --- | --- |
| `nome` | Nome informado |
| `peso` | Peso em quilogramas |
| `altura` | Altura em metros |
| `dataNascimento` | Data ISO (`yyyy-MM-dd`) |
| `idade` | Idade calculada quando o perfil é salvo |

As preferências de treino também ficam no documento:

- cada `firestoreKey` de exercício recebe o estado `"Selecionado"` ou `"Excluído"`;
- a quantidade configurada de séries é armazenada no campo `${firestoreKey}_seriesTotal`;
- a chave do último vídeo selecionado é gravada no campo `video`.

As gravações usam merge para preservar campos não relacionados no documento. IDs e chaves existentes do catálogo são mantidos estáveis para compatibilidade com dados já salvos.

O app não persiste histórico de sessões. Carga digitada, séries concluídas, conclusão manual e tempos dos cronômetros permanecem no estado dos objetos de exercício durante a sessão; não são registros históricos no Firestore.

### Comportamento em falha de leitura

Se a leitura do documento do usuário falhar durante o carregamento de exercícios, `WorkoutRepository` chama o callback com o catálogo local e não propaga a falha. Isso mantém a lista disponível, mas pode ocultar que as preferências remotas não foram carregadas.

## Tecnologias e requisitos

| Item | Versão ou configuração |
| --- | --- |
| Linguagem | Java 8 |
| Gradle Wrapper | 8.13 |
| Android Gradle Plugin | 8.13.2 |
| `minSdk` | 21 |
| `compileSdk` / `targetSdk` | 33 / 33 |
| Application ID | `com.example.meutreino` |
| Firebase BoM | 32.1.0 |
| Material Components | 1.9.0 |
| AndroidX Media3 | 1.0.2 |

O projeto usa AndroidX AppCompat, Core, Lifecycle/LiveData/ViewModel, Navigation e ConstraintLayout, além de View Binding, Firebase Analytics, Firebase Authentication, Cloud Firestore, JUnit e AndroidX Test.

## Configuração do ambiente

1. Instale o Android Studio e o Android SDK necessário ao `compileSdk 33`.
2. Configure `local.properties` com o caminho local do Android SDK.
3. Configure um projeto Firebase e o arquivo `app/google-services.json` correspondente ao application ID `com.example.meutreino`.
4. Abra a pasta do projeto no Android Studio e aguarde a sincronização do Gradle.
5. Execute o app em um emulador ou dispositivo compatível.

O arquivo Firebase precisa corresponder ao projeto autorizado para desenvolvimento. Não publique credenciais, segredos ou arquivos de configuração privada.

## Build e testes

Para executar as verificações disponíveis:

```bash
bash gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
```

As tarefas também podem ser executadas separadamente:

```bash
bash gradlew :app:testDebugUnitTest
bash gradlew :app:assembleDebug
bash gradlew :app:assembleDebugAndroidTest
bash gradlew :app:lintDebug
```

`assembleDebugAndroidTest` compila e empacota o APK de testes instrumentados, mas não executa esses testes. Para executá-los, conecte um dispositivo ou emulador e rode:

```bash
bash gradlew :app:connectedDebugAndroidTest
```

### Cobertura automatizada existente

`WorkoutModelTest` verifica:

- cálculo de IMC, classificação e estimativa de água;
- idade a partir da data de nascimento e rejeição de data inválida;
- conclusão de exercício por séries ou manualmente;
- mínimo de exercícios por categoria e unicidade de IDs, chaves Firestore e chaves de vídeo;
- mapeamento das cinco imagens abdominais e ausência de arte incompatível em exercícios sem imagem.

`ExampleInstrumentedTest` verifica o contexto e o application ID em um ambiente Android. Os testes existentes não substituem validação visual em dispositivo nem testes de integração com Firebase.

## Estrutura do projeto

```text
MeuTreino/
├── app/
│   ├── build.gradle
│   ├── google-services.json
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/meutreino/
│       │   │   ├── data/
│       │   │   │   ├── model/
│       │   │   │   └── repository/
│       │   │   └── ui/
│       │   │       ├── auth/
│       │   │       ├── home/
│       │   │       ├── main/
│       │   │       ├── profile/
│       │   │       ├── splash/
│       │   │       ├── video/
│       │   │       └── workout/
│       │   └── res/
│       │       ├── color/ drawable/ layout/ menu/
│       │       ├── mipmap*/ navigation/ raw/
│       │       ├── values*/ xml/
│       │       └── ...
│       ├── test/java/com/example/meutreino/
│       └── androidTest/java/com/example/meutreino/
├── docs/
│   ├── analise-projeto.md
│   └── plantuml/
├── gradle/wrapper/
├── README.md
├── readme2.md
├── readme3.md
├── build.gradle
├── settings.gradle
└── gradle.properties
```

`local.properties` guarda o caminho do Android SDK da máquina. Diretórios `.gradle/`, `build/` e arquivos `.DS_Store` são ignorados pelo `.gitignore`.

## Limitações e cuidados

- O IMC é uma métrica geral e não avalia composição corporal ou estado clínico.
- A estimativa de água (35 ml/kg) não é uma prescrição individual.
- O catálogo não contém vídeo demonstrativo para todas as chaves.
- A configuração da sessão não é um diário persistente de treino.
- Regras de acesso do Firestore precisam ser configuradas no Firebase; o app não as define neste repositório.
- Não há painel para profissionais, gestão de academia, vários perfis ou academias, socialização ou estatísticas históricas.
- Os testes automatizados cobrem modelos e inicialização básica, não todos os fluxos visuais, erros de rede ou integrações Firebase.
- Uma falha de leitura de preferências de treino pode resultar no uso do catálogo local sem notificação da falha remota.
- Na exclusão da conta, uma falha ao remover a conta do Firebase Authentication pode ocorrer depois que o perfil no Firestore já foi removido.
- As referências acadêmicas contextualizam o tema, mas não comprovam a eficácia deste aplicativo específico.

## Documentação complementar

Os documentos a seguir detalham aspectos específicos da implementação:

- [Análise técnica do projeto](./docs/analise-projeto.md)
- [Diagrama de classes detalhado](./docs/plantuml/diagrama-classes.puml)
- [Diagrama essencial de classes](./docs/plantuml/diagrama-classes-essencial.puml)
- [Diagrama de sequência do sistema](./docs/plantuml/diagrama-sequencia-sistema.puml)
- [Organização da arquitetura](./docs/plantuml/organizacao-arquitetura.puml)

## Metodologia e validação acadêmica

O desenvolvimento descrito no projeto passou pela concepção do problema, organização da interface, implementação Android e validação técnica. O processo pode ser resumido em:

1. Levantamento dos fluxos de autenticação, perfil e organização de exercícios.
2. Criação e ajuste de telas com layouts XML e componentes Material.
3. Implementação em Java, separando apresentação, modelos e repositórios.
4. Integração com Firebase Authentication e Cloud Firestore.
5. Testes unitários, build Android, compilação de testes instrumentados e lint.

A validação automatizada verifica regras de modelo e catálogo e assegura que o projeto compile. Isso não equivale a teste de usabilidade com participantes.
