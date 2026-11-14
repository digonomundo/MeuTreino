# Política de Segurança

## Versões com suporte

O projeto não publica versões formais nem define um calendário de manutenção. Relatos referentes ao estado mais recente da branch `main` são considerados; versões locais ou antigas podem não receber correções.

| Versão | Suporte |
| --- | --- |
| `main` (estado atual) | Análise e correções conforme a disponibilidade da pessoa mantenedora |
| Versões anteriores ou builds locais | Sem garantia de suporte |

## Reportar uma vulnerabilidade

**Não publique vulnerabilidades, credenciais, dados de usuários ou passos exploráveis em uma issue ou pull request pública.**

Envie o relato em particular para [rodrigo@digonomundo.com](mailto:rodrigo@digonomundo.com). Se o e-mail não estiver disponível, use o recurso privado de reporte de vulnerabilidades do GitHub. Não havendo um canal privado disponível, peça um contato privado sem divulgar detalhes técnicos publicamente.

Inclua, quando possível:

- o commit ou a versão afetada;
- o impacto observado e os componentes envolvidos;
- passos mínimos para reprodução em uma conta e ambiente de teste;
- logs ou evidências sanitizados, sem dados pessoais, tokens ou credenciais.

Não teste vulnerabilidades contra contas, dados ou serviços de terceiros. Não inclua chaves de serviço, senhas, tokens, dados de usuários ou arquivos de configuração privados no relato.

A pessoa mantenedora tentará analisar o relato e coordenar eventuais correções e divulgação com quem o enviou. Não há prazo de resposta ou garantia de correção definido.

## Considerações específicas do MeuTreino

- O app usa Firebase Authentication e Cloud Firestore. Operações de troca de e-mail, troca de senha e exclusão de conta exigem reautenticação no fluxo implementado.
- Os dados do perfil são associados a `usuarios/{uid}` no Firestore. Regras de acesso do Firestore precisam ser configuradas no projeto Firebase; elas não são definidas por este repositório.
- `local.properties` e `app/google-services.json` são configurações locais ignoradas pelo Git. Nunca inclua chaves privadas de Firebase Admin ou credenciais de serviço no aplicativo ou no repositório.
- A análise de segurança deve considerar que o aplicativo Android é um cliente e não substitui controles de autorização configurados no Firebase.
