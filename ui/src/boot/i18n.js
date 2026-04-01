import { defineBoot } from '#q-app/wrappers'
import { createI18n } from 'vue-i18n'

export default defineBoot(async ({app}) => {
  const config = await fetch('/config/i18n.json')
    .then((res) => res.json());

  const messages = {};

  for (const lang of Object.keys(config.languages)) {
    const url = config.languages[lang];

    messages[lang] = await fetch(url)
      .then((res) => res.json());
  }

  const i18n = createI18n({
    locale: 'en-US',
    globalInjection: true,
    messages
  })

  // Set i18n instance on app
  app.use(i18n)
})
