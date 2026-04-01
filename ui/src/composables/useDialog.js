import { onBeforeUnmount, onMounted, onUnmounted, ref, watch } from "vue";
import UiEvent from "src/events/UiEvent.js";
import interact from 'interactjs';

export function useDialog(key, onOpen) {
  const show = ref(false);
  const dialogRef = ref(null);
  let dialogSubscription;
  let interactable = null;

  function onDialogEvent(dialogEvent) {
    if (dialogEvent.key !== key) {
      return;
    }

    const data = dialogEvent.data;

    if (data.type === 'close') {
      show.value = false;
      return;
    }

    show.value = true;

    onOpen?.(data);
  }

  watch(dialogRef, () => {
    if (!interactable && dialogRef.value.$el) {
      initInteract();
    }
  });

  function initInteract() {
    const target = dialogRef.value?.$el;

    interactable = interact(target).draggable({
      allowFrom: '.drag-handle',

      listeners: {
        move(event) {
          const target = event.target

          const x =
            (parseFloat(target.getAttribute('data-x')) || 0) + event.dx

          const y =
            (parseFloat(target.getAttribute('data-y')) || 0) + event.dy

          target.style.transform =
            `translate(${x}px, ${y}px)`

          target.setAttribute('data-x', x)
          target.setAttribute('data-y', y)
        }
      },
      modifiers: [
        interact.modifiers.restrictRect({
          restriction: 'parent',
          endOnly: true
        })
      ]
    });
  }

  onMounted(() => {
    dialogSubscription = UiEvent.subscribe(onDialogEvent);
  });

  onBeforeUnmount(() => {
    interactable?.unset();
  });
  onUnmounted(() => {
    dialogSubscription?.unsubscribe();
  });

  return {show, dialogRef};
}
