# 🌸 Pilates Espaço Mulher

Aplicativo profissional desenvolvido em **Kotlin** e **Jetpack Compose** para gestão clínica, avaliação postural, bioimpedância e acompanhamento de pacientes da clínica **Pilates Espaço Mulher** (Dra. Rogéria Collares).

---

## 📱 Como Baixar e Instalar o APK pelo GitHub

### Opção 1: Baixar via GitHub Releases (Recomendado)
1. Acesse a aba [**Releases**](https://github.com/RogeriaCollares/PilatesEspacoMulher/releases) deste repositório.
2. Na versão mais recente, clique no arquivo **`PilatesEspacoMulher.apk`**.
3. Abra o arquivo baixado no seu celular Android.
4. Se o sistema solicitar, permita a instalação a partir desta fonte (*Configurações > Permitir desta fonte*).
5. Clique em **Instalar** e abra o app!

### Opção 2: Baixar pelo GitHub Actions
1. Vá até a aba [**Actions**](https://github.com/RogeriaCollares/PilatesEspacoMulher/actions).
2. Clique no workflow mais recente de **Build and Release APK**.
3. Na seção **Artifacts**, baixe o pacote **`PilatesEspacoMulher-APK`**.

---

## 🚀 Funcionalidades Principais

- 🔒 **Proteção Biométrica (FaceID / Fingerprint):** Prontuários e dados confidenciais dos pacientes protegidos com autenticação nativa do Android ao abrir o app.
- 📋 **Cadastro e Ficha do Paciente:** Gestão completa de dados cadastrais e histórico clínico com transições visuais suaves.
- 📐 **Avaliações Clínicas e Posturais:** Anamnese, avaliação postural (frontal, lateral, posterior) e bioimpedância com cálculo automático de IMC.
- 🔔 **Lembretes e Reavaliações Automáticas:** Agendamento de notificações locais nativas (AlarmManager) para alertar sobre datas de reavaliação às 08:00 do dia marcado, funcionando 100% offline.
- 🏋️ **Biblioteca e Fichas de Treino:** Criação e personalização de treinos específicos para cada aluna/paciente.
- 📄 **Exportação de Relatórios em PDF:** Geração e compartilhamento instantâneo do laudo de avaliação em PDF para impressão ou envio no WhatsApp.
- 🎨 **Temas Personalizáveis:** Escolha entre 3 identidades visuais exclusivas (*Professional Purple*, *Forest Green* e *Slate Blue*) com suporte a modo claro e escuro.
- 🔄 **Notificador de Atualizações Integrado:** O app verifica automaticamente no GitHub se existe uma versão mais recente e exibe um alerta interativo no menu inicial.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin 2.x
- **UI:** Jetpack Compose (Material Design 3)
- **Persistência Local:** Room Database (SQLite)
- **Segurança:** AndroidX Biometric API
- **Notificações:** Android Notifications & AlarmManager
- **CI/CD:** GitHub Actions (compilação e release automatizados do `.apk`)
