<%@ page import="

com.psddev.cms.db.Content,
com.psddev.cms.db.ToolUi,
com.psddev.cms.tool.ToolPageContext,

com.psddev.dari.db.ObjectField,
com.psddev.dari.db.ObjectFieldComparator,
com.psddev.dari.db.State,
com.psddev.dari.db.ObjectType,
com.psddev.dari.db.Query,
com.psddev.dari.util.ObjectUtils,
com.psddev.dari.util.PaginatedResult,
com.psddev.dari.util.StorageItem,

java.util.ArrayList,
java.util.Collections,
java.util.Date,
java.util.List,
java.util.Set,
java.util.UUID
" %><%

// --- Logic ---

ToolPageContext wp = new ToolPageContext(pageContext);

State state = State.getInstance(request.getAttribute("object"));

ObjectField field = (ObjectField) request.getAttribute("field");
String fieldName = field.getInternalName();
Object fieldValue = state.getValue(fieldName);

ObjectType fieldValueType = null;
Set<ObjectType> validTypes = field.findConcreteTypes();
if (validTypes.size() == 1) {
    for (ObjectType type : validTypes) {
        fieldValueType = type;
        break;
    }
}

String inputName = (String) request.getAttribute("inputName");
String idName = inputName + ".id";
String typeIdName = inputName + ".typeId";
String publishDateName = inputName + ".publishDate";

UUID id = wp.uuidParam(idName);
UUID typeId = wp.uuidParam(typeIdName);
Date publishDate = wp.dateParam(publishDateName);

if ((Boolean) request.getAttribute("isFormPost") && fieldValue != null && !State.getInstance(fieldValue).getTypeId().equals(typeId)) {
    fieldValue = null;
}

if ((Boolean) request.getAttribute("isFormPost")) {
    fieldValue = Query.findById(Object.class, id);
    state.putValue(fieldName, fieldValue);
    return;
}

// --- Presentation ---

%><div class="smallInput">
    <% wp.objectSelect(field, fieldValue, "name", inputName); %>
</div>
