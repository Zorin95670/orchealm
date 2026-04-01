export function useColor() {

  function toRGB(hexColor) {
    const r = parseInt(hexColor.slice(1, 3), 16);
    const g = parseInt(hexColor.slice(3, 5), 16);
    const b = parseInt(hexColor.slice(5, 7), 16);

    return [r, g, b];
  }

  function toRGBA(hexColor, opacity) {
    return [...toRGB(hexColor), opacity];
  }

  return {
    toRGB,
    toRGBA,
  }
}
