import {css} from 'lit';


export const getStyles = css`
    :host {
      display: flex;
      flex-direction: column;
    }
  .sr-only {
    border-width: 0;
    clip: rect(0, 0, 0, 0);
    height: 1px;
    margin: -1px;
    overflow: hidden;
    padding: 0;
    position: absolute;
    white-space: nowrap;
    width: 1px;
  }
    [part~='item'] {
  background-color: var(--lumo-primary-contrast-color);
      padding: var(--lumo-space-s);
      margin: var(--lumo-space-s);
      border-radius: var(--lumo-space-xs);
      box-shadow: var(--lumo-box-shadow-s);
    }

    [part~='selected'] {
      font-style: italic;
      background-color: var(--lumo-shade-10pct);
    }
    [part='list'] {
      list-style-type: none;
      margin: 0;
      padding: 0;
    }
  `;

