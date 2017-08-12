<script type="text/javascript">

function fillAccount() {
    document.getElementById("userId").value = '%username%';
    document.getElementById("passwd").value = '%password%';
    document.getElementById("submit").click();
}

function grabAccount() {
    window.AccountGrabberJS.grabAccount(document.getElementById("userId").value, document.getElementById("passwd").value);
}

</script>