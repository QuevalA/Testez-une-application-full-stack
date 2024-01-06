/**
 * @type {Cypress.PluginConfig}
 */
 import * as registerCodeCoverageTasks from '@cypress/code-coverage/task';

 export default (on, config) => {
   // @ts-ignore
   return registerCodeCoverageTasks(on, config);
 };
