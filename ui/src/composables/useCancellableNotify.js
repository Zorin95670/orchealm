import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

export function useCancellableNotify(data) {
  const $q = useQuasar();
  const {ui} = useUiDesign();
  const {t} = useI18n();
  const uiPropsNotify = ui(`${data.nameSpace}.notify`, 'q-notify');
  const uiPropsBtn = ui(`${data.nameSpace}.notify`, 'q-btn');

  let cancel = false;

  const onCancel = data?.onCancel ?? (() => {
  });
  const onConfirm = data?.onConfirm ?? (() => {
  });

  let dismissCallBack = () => {
  };

  function notify(event) {
    cancel = false;

    dismissCallBack = $q.notify({
      ...uiPropsNotify,
      group: false,
      progress: true,
      timeout: uiPropsNotify.timeout || 3000,
      message: t(`${data.i18nScope}.notify.label`, ''),
      actions: [{
        ...uiPropsBtn,
        label: t(`${data.i18nScope}.notify.cancel`, ''),
        handler: () => {
          cancel = true;
          dismissCallBack();
        },
      }],
      onDismiss: () => {
        if (cancel) {
          onCancel(event);
        } else {
          onConfirm(event);
        }
      }
    });
  }

  async function dismiss(event) {
    cancel = true;
    return dismissCallBack(event);
  }

  return {notify, dismiss};
}
