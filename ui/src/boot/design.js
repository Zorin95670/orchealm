import { defineBoot } from '#q-app/wrappers'

let design;

export default defineBoot(async () => {
  const response = await fetch('/config/design.json');

  if (!response.ok) {
    throw new Error('Failed to fetch design');
  }

  design = await response.json();
});

export { design };
