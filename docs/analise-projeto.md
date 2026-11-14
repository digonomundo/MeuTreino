# Análise técnica do MeuTreino

Esta análise descreve o estado atual do código e complementa a documentação geral em [README.md](../README.md). Os fluxos citados foram conferidos com as Activities, Fragments, repositórios, Manifest, recursos e grafo de navegação do projeto.

## Visão geral

MeuTreino é um app Android em Java para organizar treinos de musculação. A aplicação usa layouts XML com View Binding, Activities e Fragments, o Navigation Component para a navegação principal, Firebase Authentication para contas e Cloud Firestore para perfil/preferências.

## Arquitetura

A organização é próxima de MVVM, sem uma camada de domínio separada:

- `ui/auth`, `ui/home`, `ui/main`, `ui/profile`, `ui/splash`, `ui/video` e `ui/workout`: interface e lógica de apresentação.
- `ui/*ViewModel`: estados expostos por `LiveData`; validações de formulário e coordenação dos repositórios.
- `data/model`: `User`, `Exercise`, `WorkoutCategory` e `ExerciseCatalog`.
- `data/repository`: abstração das chamadas ao Firebase Authentication e Cloud Firestore.
- `res`: layouts, menus, tema, imagens, vídeos e grafo de navegação.

O fluxo predominante é Activity/Fragment → ViewModel → Repository → Firebase. O catálogo de exercícios é local; o Firestore guarda perfil e preferências por conta.

## Inicialização e navegação

`SplashActivity` verifica se existe usuário autenticado. Com sessão ativa, abre `MainActivity`; sem sessão, abre `LoginActivity`. O cadastro cria a conta Firebase e encaminha para `UserDataInputActivity`, que permite preencher o perfil ou pular essa etapa. O login bem-sucedido abre diretamente `MainActivity`.

`MainActivity` configura o `NavHostFragment` com `mobile_navigation.xml` e um `NavigationView`. O destino inicial é a Home; o menu lateral contém início, perfil e segurança. A Home abre as seis categorias e os fluxos de montagem correspondentes.

## Perfil e saúde

O documento Firebase do usuário usa `usuarios/{uid}`. `UserRepository` lê e grava nome, peso, altura, data de nascimento e idade calculada. As gravações usam `SetOptions.merge()`.

`UserDataInputActivity` exige nome, peso, altura e data válida para salvar. O seletor não permite datas futuras e a idade é calculada a partir da data de nascimento. `UserProfileFragment` indica perfil incompleto quando faltam dados, calcula IMC apenas com peso e altura válidos e apresenta estimativa de água baseada em 35 ml/kg. A tela permite reabrir a edição mesmo após pular o cadastro.

## Catálogo e treino

`ExerciseCatalog.ALL_EXERCISES` contém 45 exercícios: peito 12; abdômen, pernas, braços, costas e ombros 6 cada, exceto abdômen, que tem 9 após a integração das imagens. IDs e chaves Firestore dos exercícios existentes são mantidos.

- `WorkoutCategoryFragment` carrega os exercícios selecionados de uma categoria e oferece acesso à demonstração.
- `WorkoutSetupFragment` permite selecionar exercícios e configurar de 1 a 20 séries, padrão três.
- `WorkoutAdapter` apresenta imagem, carga, séries, cronômetro crescente por exercício, descanso regressivo (60 segundos padrão) e conclusão manual.
- `WorkoutRepository` recupera configuração do documento do usuário, marca a seleção por `firestoreKey` e salva a quantidade no campo `${firestoreKey}_seriesTotal`.

Seleção e quantidade de séries são persistidas. Carga, séries concluídas, conclusão manual e cronômetros não são salvos como histórico: permanecem no estado dos objetos usados na sessão.

Se a leitura Firestore para carregar os exercícios falhar, `WorkoutRepository` retorna a lista local da categoria sem propagar o erro. Isso preserva a exibição do catálogo, mas pode esconder que as preferências do usuário não foram carregadas.

## Vídeos e imagens

`VideoPlayerFragment` exibe instruções e reproduz vídeos com Media3/ExoPlayer. O mapeamento implementado contempla `video1`–`video12` e `video34`; as demais chaves mostram o estado de vídeo indisponível. Arquivos presentes em `res/raw` que não estejam nesse mapeamento não são selecionáveis pelo player.

O catálogo mantém `imageResId == 0` para exercícios sem imagem compatível, e `WorkoutAdapter` remove o drawable ao reciclar uma célula sem imagem para impedir que apareça a imagem de outro exercício. Para abdômen, cinco imagens foram associadas a elevação de pernas no banco declinado, crunch declinado, abdominal na bola, abdominal no banco declinado e abdominal na polia. A imagem originalmente nomeada como oblíquo não evidencia rotação; a descrição foi corrigida para crunch declinado.

## Segurança e limitações

`AccountSecurityFragment` exige a senha atual para e-mail, senha e exclusão. `AccountSecurityRepository` reautentica com credencial de e-mail/senha. A troca de e-mail chama `verifyBeforeUpdateEmail`; a troca de senha usa `updatePassword`. A exclusão remove primeiro `usuarios/{uid}` e depois a conta Firebase. Se a exclusão no Authentication falhar após remover o documento, a conta poderá permanecer sem o perfil no Firestore; o erro é informado.

Limitações observadas:

- Não há painel de instrutor, gestão de academia, recursos sociais ou histórico persistido de sessões.
- Nem todas as chaves de vídeo têm demonstração mapeada.
- A leitura de preferências de treino usa fallback silencioso para o catálogo local em caso de falha de rede.
- As métricas de IMC e hidratação são informativas e não substituem orientação profissional.

## Validação

O projeto oferece teste unitário `WorkoutModelTest` para indicadores, idade, conclusão, catálogo, unicidade de chaves e mapeamento de imagens; `ExampleInstrumentedTest` verifica contexto e application ID. As tarefas Gradle verificadas incluem testes unitários, build Debug, compilação de AndroidTest e lint. A compilação de AndroidTest não executa testes instrumentados: para isso é necessário dispositivo/emulador e `:app:connectedDebugAndroidTest`.

## Diagramas

- [Diagrama de classes detalhado](./plantuml/diagrama-classes.puml)
- [Diagrama de classes essencial](./plantuml/diagrama-classes-essencial.puml)
- [Diagrama de sequência do sistema](./plantuml/diagrama-sequencia-sistema.puml)
- [Organização da arquitetura](./plantuml/organizacao-arquitetura.puml)
