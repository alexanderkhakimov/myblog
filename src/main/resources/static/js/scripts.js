function editComment(commentId) {
    const commentDiv = document.getElementById('comment-' + commentId);
    const content = commentDiv.querySelector('p').innerText;
    commentDiv.innerHTML = `
        <form action="/comments/${commentId}" method="post">
            <textarea name="content" required>${content}</textarea>
            <button type="submit">Save</button>
        </form>
    `;
}