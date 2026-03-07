# Patch Pack for `task-manager-api`

Este pacote inclui os ficheiros novos e os ficheiros corrigidos que mais impacto têm na qualidade percebida do projeto.

## O que foi melhorado

1. **README mais rigoroso**
   - menos marketing excessivo
   - mais alinhado com o que o projeto realmente é
   - instruções consistentes com o estado atual do repositório

2. **Migrações com Flyway**
   - saída de `ddl-auto: update`
   - entrada em `ddl-auto: validate`
   - schema versionado em `src/main/resources/db/migration/`

3. **Autenticação mais robusta**
   - tratamento explícito de JWT inválido ou expirado
   - resposta `401 Unauthorized` mais limpa e previsível

4. **Normalização de email**
   - trim + lowercase antes de persistir/autenticar
   - reduz edge cases de duplicados e login inconsistente

5. **Tratamento de erros melhorado**
   - mensagens mais úteis para enums inválidos em query params ou JSON

6. **Pipeline de CI melhorada**
   - `mvn clean verify`
   - upload de relatórios de testes
   - upload de relatório JaCoCo

7. **Higiene de repositório**
   - `LICENSE`
   - `.editorconfig`
   - `.gitattributes`
   - `.env.example`

## Ficheiros a substituir

Substitui no teu projeto os ficheiros que vieram neste pacote, mantendo exatamente os mesmos caminhos.

## Ficheiros novos

- `LICENSE`
- `.editorconfig`
- `.gitattributes`
- `.env.example`
- `src/main/resources/db/migration/V1__init_schema.sql`
- `src/main/java/com/mprazeres/taskmanager/security/RestAuthenticationEntryPoint.java`

## O que ficou de fora de propósito

### Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/`)
Não o incluí aqui porque o ambiente onde fiz a análise não tinha Maven instalado nem acesso externo para gerar o wrapper oficial.

Gera-o localmente com:

```bash
mvn -N wrapper:wrapper
```

Depois faz commit destes ficheiros:
- `mvnw`
- `mvnw.cmd`
- `.mvn/wrapper/*`

## Ordem recomendada

1. substitui os ficheiros
2. gera o Maven Wrapper
3. corre `mvn clean verify`
4. corre `docker compose up --build`
5. testa Swagger em `/swagger-ui.html`
6. só depois faz push para GitHub
