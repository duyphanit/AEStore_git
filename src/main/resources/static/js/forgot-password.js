document.addEventListener("DOMContentLoaded", function() {
    var forgotModal = document.getElementById('forgotPasswordModal');
    var forgotLink = document.getElementById('forgot-password-link');
    if (!forgotModal) return;

    // Lấy các biến trạng thái từ data-attribute
    var block = forgotModal.getAttribute('data-block') === 'true';
    var blockMs = parseInt(forgotModal.getAttribute('data-block-ms')) || 0;
    var cooldown = forgotModal.getAttribute('data-cooldown') === 'true';
    var cooldownMs = parseInt(forgotModal.getAttribute('data-cooldown-ms')) || 0;

    // Xử lý block
    if (block && blockMs > 0) {
        alert("Bạn đã thực hiện quá 3 lần. Vui lòng thử lại sau 15 phút!");
        if (forgotLink) forgotLink.classList.add('disabled');
        setTimeout(function() {
            if (forgotLink) forgotLink.classList.remove('disabled');
        }, blockMs);
        return;
    }

    // Xử lý cooldown
    if (cooldown && cooldownMs > 0) {
        var myModal = new bootstrap.Modal(forgotModal);
        myModal.show();
        var timerSpan = document.getElementById('cooldown-timer');
        var sendBtn = document.getElementById('send-btn');
        function updateTimer() {
            if (!timerSpan) return;
            var seconds = Math.floor(cooldownMs / 1000);
            var min = Math.floor(seconds / 60);
            var sec = seconds % 60;
            timerSpan.textContent = min + ":" + (sec < 10 ? "0" : "") + sec;
            if (cooldownMs > 0) {
                sendBtn.disabled = true;
                cooldownMs -= 1000;
                setTimeout(updateTimer, 1000);
            } else {
                sendBtn.disabled = false;
                timerSpan.textContent = "";
            }
        }
        updateTimer();
    }
});
