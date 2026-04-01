import { defineBoot } from "#q-app/wrappers";

let appConfig;

export default defineBoot(async () => {
  const response = await fetch('/config/config.json');

  if (!response.ok) {
    throw new Error('Failed to fetch design');
  }

  appConfig = await response.json();
});

export { appConfig };
