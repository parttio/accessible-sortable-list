import {LitElement, html, css, PropertyValues} from "lit";
import {customElement, property, query, state} from 'lit/decorators.js';
import {repeat} from 'lit/directives/repeat.js';
import {animate, fadeOut, flyBelow} from '@lit-labs/motion';
import {getStyles} from "./sortable-list-styles";

export default interface ListItemInterface {
  id: number;
  description: string;
}

enum Key {
  SPACE= "Space",
  UP_ARROW= "ArrowUp",
  DOWN_ARROW = "ArrowDown",
  ESC = "Escape"
}
@customElement('sortable-list')
class SortableList extends LitElement {

  static styles = getStyles;

  @property({type: Array}) items:ListItemInterface[] = [];

  @state()
  private _sortedItems = this.items;

  @state()
  private _dragItem?:ListItemInterface;

  @state()
  private _dragItemIndex?:number;

  @state()
  private _keyboardDragMode:boolean = false;

  @state()
  private dropItemIndex?:number;

  @state()
  private _currentIndex:number = 0;

  @state()
  private _liveText:string = "";

  private _focusedElement?: HTMLElement;

  @query('#list')
  private _list?:HTMLElement;

  private translations = {
    operation : "Press Spacebar to reorder",
    title : "Sortable list",
    liveTextGrabbed: "${itemDescription}, grabbed. Current position in list: ${dragItemIndex} of ${itemsLength}. Press up and down arrow keys to change position, Spacebar to drop, Escape key to cancel.",
    liveTextDropped: "${itemDescription}, dropped. Final position in list:  ${dropItemIndex} of ${itemsLength}.",
    liveTextCancelled: "${itemDescription} reorder cancelled.",
  }

  render() {
    return html`
      <div aria-live="assertive" class="sr-only">${this._liveText}</div>
      <div id="operation" class="sr-only">
        ${this.translations.operation}
      </div>
      <div id="title" class="sr-only">${this.translations.title}</div>
      <ol id="list" part="list" role="listbox" aria-labelledby="title"
          aria-describedby="operation"
           @drop="${ (e:DragEvent) => this._onDropAction(e)}"
           @dragover="${ (e:DragEvent) => this._onDragOverAction(e)}"
          >
        ${repeat(
          this._sortedItems,
         item => item.id,
          (item, index) => html`
        <li part="item ${(this._dragItem && this._dragItem.id == item.id)? "selected": ""}" 

            aria-describedby="operation"
            @keydown=${(e:KeyboardEvent) => this._keyboardHandler(e, item)}
            tabindex="${((this._keyboardDragMode)?(this._dragItem && this._dragItem.id == item.id):(this._currentIndex == index))? "0": "-1"}"
            aria-selected="${((this._keyboardDragMode)?(this._dragItem && this._dragItem.id == item.id):(this._currentIndex == index))}"
            role="option"
            data-index=${index}
            draggable="${this._dragItemIndex === undefined}"
            @dragstart="${(e: DragEvent) => this._onDragAction(e, item, index)}"
            @dragend="${ (e:DragEvent) => this._onDragEndAction(e)}">
              ${item.description}
        </li>`)}
      </ol>`;
  }

  private _keyboardHandler(event: KeyboardEvent, item: ListItemInterface) {

    if (event.metaKey || event.ctrlKey) {
      return;
    }

    if (event.code === Key.SPACE) {

      // space key - toggle mode Normal <-> Drag and drop
      if (this._dragItemIndex == undefined) {
        // Drag and drop mode
        if ((event.target instanceof Element) && (event.target as Element).hasAttribute("data-index")) {
          this._keyboardDragMode = true;
          const index = event.target.getAttribute("data-index");
          if (index !== undefined) {
            const numericIndex = Number(index);
            this._dragItemIndex = numericIndex;
            this.dropItemIndex = numericIndex;
            this._dragItem = item;
            this._liveText = this._interpolateTemplate(this.translations.liveTextGrabbed);
          }
        }
      } else {
        // Back to normal mode
        // reorder if needed
        if (this.dropItemIndex !== undefined) {
          this._liveText = this._interpolateTemplate(this.translations.liveTextDropped);
          this.items = [...this._reorderItems()];
          this._currentIndex = this.dropItemIndex;
          this.dispatchEvent(
              new CustomEvent('sortable-list-reordered', { detail: { fromIndex: this._dragItemIndex, toIndex: this.dropItemIndex } })
          );
        }
        this._resetKeyboardDragMode();
      }
      event.preventDefault();
    } else if (event.key === Key.UP_ARROW) {
      this._keyboardPrevious(event.target as HTMLElement);
      event.preventDefault();
    } else if (event.key === Key.DOWN_ARROW) {
      this._keyboardNext(event.target as HTMLElement);
      event.preventDefault();
    } else if (event.key === Key.ESC) {
      this._liveText = this._interpolateTemplate(this.translations.liveTextCancelled);;
      this._resetKeyboardDragMode();
      event.preventDefault();
    }
  }

  private _resetKeyboardDragMode() {
    this._keyboardDragMode = false;
    this._dragItemIndex = undefined;
    this.dropItemIndex = undefined;
    this._dragItem = undefined;
    this._focusedElement = undefined;
  }

  private _onDragAction(event: DragEvent, todo: ListItemInterface, index: number) {
    if (event.dataTransfer && event.target && event.currentTarget) {
      event.dataTransfer
        .setData('text/plain', String(todo.id));

      if (todo) {
        this._dragItemIndex =  index;
      }
    }
  }

  private _onDragEndAction(event: DragEvent) {
    if (event.target && event.currentTarget) {
      this._dragItemIndex = undefined;
      this.dropItemIndex = undefined;
    }
  }
  private _reorderItems() {
    if (this._dragItemIndex !== undefined && this.dropItemIndex !== undefined && (this.dropItemIndex !== this._dragItemIndex)) {
      return this._moveElementInArray([...this.items], this._dragItemIndex, this.dropItemIndex);
    } else {
      return [...this.items];
    }
  }
  private _moveElementInArray(arr: Array<ListItemInterface>, fromIndex: number, toIndex: number) {
    const newArr = [...arr];
    newArr.splice(toIndex, 0, newArr.splice(fromIndex, 1)[0]);
    return newArr;
  }
  private _onDragOverAction(event: DragEvent) {
    event.preventDefault();
    // set the new index
    if (event.target instanceof Element) {
      const before = (event.offsetY - (event.target.clientHeight / 2)) < 1;
      if ( event.target.hasAttribute("data-index")) {
        const index = event.target.getAttribute("data-index");
        if (index !== undefined) {
          const numericIndex = Number(index);
          this.dropItemIndex = (before && numericIndex > 0) ? numericIndex - 1 : numericIndex;
        }
      }
    }
  }
  private _onDropAction(event:DragEvent) {
    if (event.dataTransfer && event.target && event.currentTarget) {
      if (this.dropItemIndex !== undefined) {
        this.items = [...this._reorderItems()];
        this.dispatchEvent(
            new CustomEvent('sortable-list-reordered', { detail: { fromIndex: this._dragItemIndex, toIndex: this.dropItemIndex } })
        );
        this._resetKeyboardDragMode();
      }
    }
  }

  private _keyboardNext(item: HTMLElement) {
    this._keyboardNavigation(item,1);
  }
  private _keyboardPrevious(item: HTMLElement) {
    this._keyboardNavigation(item, -1);
  }

  private _keyboardNavigation(item: HTMLElement, increment: -1 | 1) {
    if (this._keyboardDragMode && this.dropItemIndex !== undefined) {
      let newIndex = this.dropItemIndex + increment;
      if (newIndex >= 0 && newIndex < this.items.length) {
        this.dropItemIndex = newIndex;
        this._focusedElement = item;
        // On Chrome + VoiceOver the item so the update is announced by the screen reader because we are focusing manually the item
        // On Safari + VoiceOver, it's not announced so we keep the announcement for now
         this._liveText = this._interpolateTemplate("${itemDescription}. Current position in list:  ${dropItemIndex} of ${itemsLength}.");
           //  `${this._dragItem?.description}. Current position in list:  ${this.dropItemIndex + 1} of ${this.items.length}.`;
      }
    } else {
      // no keyboardMode
      let newIndex = this._currentIndex + increment;
      if (newIndex >= 0 && newIndex < this.items.length) {
        this._currentIndex = newIndex;
        // focus the item
        if (this._list) {
          if (this._list.children[this._currentIndex] instanceof HTMLElement) {
            (this._list.children[this._currentIndex] as HTMLElement).focus();
          }
        }
      }
    }
  }
  private _interpolateTemplate(template: string)  {
    const args = {
      itemDescription: this._dragItem?.description,
      itemsLength: this.items.length,
      dropItemIndex: this.dropItemIndex! + 1,
      dragItemIndex: this._dragItemIndex! + 1,
    };
    return Object.entries(args).reduce(
        (result, [arg, val]) => result.replace(`$\{${arg}}`, `${val}`),
        template,
    )
  }

  updated(changedProperties: Map<string, any>) {

    if (changedProperties.has('_keyboardDragMode')) {
      // focus the item
      if (this._list) {
        if (this._list.children[this._currentIndex] instanceof HTMLElement) {
          (this._list.children[this._currentIndex] as HTMLElement).focus();
        }
      }
    } else if (changedProperties.has('dropItemIndex')) {
      // refocus the element if needed
      if (this._focusedElement) {
        this._focusedElement.focus();
      }
    }
  }

  willUpdate(changedProperties: PropertyValues<this>) {
    // only need to check changed properties for an expensive computation.
    if (changedProperties.has('dropItemIndex') && !changedProperties.has('items') ) {
      // don't recalculate when the data is updated
      this._sortedItems = [...this._reorderItems()];
    } else if (!changedProperties.has('dropItemIndex') && changedProperties.has('items')) {
      this._sortedItems = [...this.items];
    }
  }
}

declare global {
  interface HTMLElementTagNameMap {
    'sortable-list': SortableList
  }
}



