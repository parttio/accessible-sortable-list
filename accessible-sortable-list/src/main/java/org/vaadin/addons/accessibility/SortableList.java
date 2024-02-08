package org.vaadin.addons.accessibility;

/*-
 * #%L
 * Accessible Sortable List
 * %%
 * Copyright (C) 2023 - 2024 Team Parttio
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@NpmPackage(value = "@lit-labs/motion", version = "1.0.7")
@JsModule("./sortable-list/sortable-list.ts")
@Tag("sortable-list")
public class SortableList<TYPE> extends Component {

    private SortableListI18n sortableListI18n;
    private List<TYPE> items = new ArrayList<>();
    private final ItemIDGenerator<TYPE> itemIDGenerator;
    private ItemLabelGenerator<TYPE> itemLabelGenerator = String::valueOf;

    public SortableList(ItemIDGenerator<TYPE> itemIDGenerator) {
        this.itemIDGenerator = itemIDGenerator;
        addClientReorderedListener(event -> reorderList(event.fromIndex, event.toIndex));
    }

    private void reorderList(int fromIndex, int toIndex) {
        TYPE element = items.get(fromIndex);
        items.remove(fromIndex);
        items.add(toIndex, element);
        fireEvent(new ReorderedEvent<>(this, true, fromIndex, toIndex, items));
    }

    public void setItems(List<TYPE> items) {
        this.items = items;
        runBeforeClientResponse(ui -> getElement()
                .executeJs("this.items = $0", convertItemsToJson()));
    }

    public void setItemLabelGenerator(ItemLabelGenerator<TYPE> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
    }

    public void setI18n(SortableListI18n sortableListI18n) {
        this.sortableListI18n = sortableListI18n;
        runBeforeClientResponse(ui -> getElement()
                .executeJs("this.translations = $0", JsonUtils.beanToJson(sortableListI18n)));
    }

    private JsonArray convertItemsToJson() {
        JsonArray array = Json.createArray();
        for (int i = 0; i < items.size(); i++) {
            JsonObject jsonObject = Json.createObject();
            jsonObject.put("id", itemIDGenerator.apply(items.get(i)).doubleValue());
            jsonObject.put("description", itemLabelGenerator.apply(items.get(i)));
            array.set(i, jsonObject);
        }
        return array;
    }
    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }
    private Registration addClientReorderedListener(ComponentEventListener<SortableListReorderedEvent<TYPE>> listener) {
        return addListener(SortableListReorderedEvent.class, (ComponentEventListener) listener);
    }
    public Registration addReorderedListener(ComponentEventListener<ReorderedEvent<TYPE>> listener) {
        return addListener(ReorderedEvent.class, (ComponentEventListener) listener);
    }
    @FunctionalInterface
    public interface ItemIDGenerator<T> extends SerializableFunction<T, Number> {

        /**
         * Gets an ID for the {@code item}.
         *
         * @param item
         *            the item to get ID for
         * @return the ID of the item, not {@code null}
         */
        @Override
        Number apply(T item);
    }

    @DomEvent("sortable-list-reordered")
    public static class SortableListReorderedEvent<T> extends ComponentEvent<SortableList<T>> {

        private final int fromIndex;
        private final int toIndex;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         */
        public SortableListReorderedEvent(SortableList<T> source, boolean fromClient,
                                          @EventData("event.detail.fromIndex") int fromIndex,
                                          @EventData("event.detail.toIndex") int toIndex) {
            super(source, fromClient);
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }
    }

    public static class ReorderedEvent<T> extends ComponentEvent<SortableList<T>> {

        private final int fromIndex;
        private final int toIndex;
        private final List<T> items;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         */
        public ReorderedEvent(SortableList<T> source, boolean fromClient,
                                          int fromIndex,
                                          int toIndex,
                              List<T> items) {
            super(source, fromClient);
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.items = items;
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }

        public List<T> getItems() {
            return items;
        }
    }

    /**
     *
     */
    public static class SortableListI18n implements Serializable {
        private String operation = "Press Spacebar to reorder";
        private String title = "Sortable list";
        private String liveTextGrabbed= "${itemDescription}, grabbed. Current position in list: ${dragItemIndex} of ${itemsLength}. Press up and down arrow keys to change position, Spacebar to drop, Escape key to cancel.";
        private String liveTextDropped= "${itemDescription}, dropped. Final position in list:  ${dropItemIndex} of ${itemsLength}.";
        private String liveTextCancelled = "${itemDescription} reorder cancelled.";

        public SortableListI18n() {
        }

        public String getOperation() {
            return operation;
        }

        public SortableListI18n setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public SortableListI18n setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getLiveTextGrabbed() {
            return liveTextGrabbed;
        }

        public SortableListI18n setLiveTextGrabbed(String liveTextGrabbed) {
            this.liveTextGrabbed = liveTextGrabbed;
            return this;
        }

        public String getLiveTextDropped() {
            return liveTextDropped;
        }

        public SortableListI18n setLiveTextDropped(String liveTextDropped) {
            this.liveTextDropped = liveTextDropped;
            return this;
        }

        public String getLiveTextCancelled() {
            return liveTextCancelled;
        }

        public SortableListI18n setLiveTextCancelled(String liveTextCancelled) {
            this.liveTextCancelled = liveTextCancelled;
            return this;
        }
    }
}
