<content:title>
    <fmt:message key="add.paragraph" />
</content:title>

<content:section cssId="storyBookParagraphCreatePage">
    <h4><content:gettitle /></h4>
    <div class="card-panel">
        <c:if test="${not empty storyBookParagraph.storyBookChapter.image}">
            <a href="<spring:url value='/content/multimedia/image/edit/${storyBookParagraph.storyBookChapter.image.id}' />">
                <img src="<spring:url value='/image/${storyBookParagraph.storyBookChapter.image.id}_r${storyBookParagraph.storyBookChapter.image.revisionNumber}.${fn:toLowerCase(storyBookParagraph.storyBookChapter.image.imageFormat)}' />" alt="${storyBookParagraph.storyBookChapter.storyBook.title}" />
            </a>
        </c:if>
        
        <form:form modelAttribute="storyBookParagraph">
            <tag:formErrors modelAttribute="storyBookParagraph" />
            
            <form:hidden path="storyBookChapter" value="${storyBookParagraph.storyBookChapter.id}" />
            <form:hidden path="sortOrder" value="${storyBookParagraph.sortOrder}" />
            <input type="hidden" name="timeStart" value="${timeStart}" />

            <div class="row">
                <div class="input-field col s12">
                    <form:label path="originalText" cssErrorClass="error"><fmt:message key='original.text' /></form:label>
                    <form:textarea path="originalText" cssClass="materialize-textarea" cssErrorClass="error" />
                </div>
            </div>

            <button id="submitButton" class="btn waves-effect waves-light" type="submit">
                <fmt:message key="add" /> <i class="material-icons right">send</i>
            </button>
        </form:form>
    </div>
</content:section>
