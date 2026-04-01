import { design } from '../boot/design.js';

const getNested = (obj, path) => {
  return path
    .split('.')
    .reduce((acc, key) => {
      if (acc && typeof acc === 'object') {
        return (acc)[key];
      }

      return undefined;
    }, obj);
}

export function useUiDesign() {
  function ui(nameSpace, localName, defaultName = localName) {
    return {
      ...design.default[defaultName],
      ...getNested(design, `${nameSpace}.${localName}`),
    };
  }

  return {ui};
}
