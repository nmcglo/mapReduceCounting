;;; salsa-mode.el --- major mode for editing SALSA code


;; Author: Wei Huang <huangw5@cs.rpi.edu>
;; Keywords: languages salsa 

;; This is free software; you can redistribute it and/or modify it
;; under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This is distributed in the hope that it will be useful, but WITHOUT
;; ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
;; or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
;; License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to the
;; Free Software Foundation, Inc., 59 Temple Place - Suite 330,
;; Boston, MA 02111-1307, USA.

;;; Commentary:

;; salsa-mode is a basic major mode for editing code conforming to the
;; SALSA1.x. 

;; BECAREFUL: This salsa-mode is a simple extension from java-mode

;; See also:
;; http://wcl.cs.rpi.edu/salsa/

;;; History:

;; 2010-04-05 Initial release.

;;; Code:

(require 'font-lock)
(require 'cc-mode)
(eval-when-compile
  (require 'regexp-opt))

(defconst salsa-mode-version "1.1"
  "SALSA Mode version number.")

(defgroup salsa nil
  "Major mode for editing SALSA code."
  :group 'languages
  :prefix "salsa-")

(defcustom salsa-mode-hook nil
  "Hook for customizing `salsa-mode'."
  :group 'salsa
  :type 'hook)

(defvar salsa-mode-map (c-make-inherited-keymap)
  "Keymap used in `salsa-mode' buffers.")

;;;###autoload
(define-derived-mode salsa-mode java-mode "SALSA"
  "Major mode for editing SALSA code.

This mode is derived from `java-mode'; see its documentation for further
information.

\\{salsa-mode-map}"
  (make-local-variable 'font-lock-defaults)
  (setq font-lock-defaults
        '((;; comment out the lines below to adjust
           ;; syntax highlighting gaudiness
           java-font-lock-keywords-1
           ;; java-font-lock-keywords-3
           salsa-font-lock-keywords-2
           salsa-font-lock-keywords-3
           )
          nil nil ((?_ . "w") (?$ . "w")) nil))

  (easy-menu-define c-salsa-menu salsa-mode-map
    "SALSA Mode Commands" (c-mode-menu "SALSA"))
  (set (make-local-variable 'indent-line-function) 'salsa-indent-line)
  (define-key salsa-mode-map ";" 'salsa-electric-semi&comma)
  (define-key salsa-mode-map "@" 'salsa-electric-semi&comma)
  )

(defvar salsa-font-lock-default-face 'salsa-font-lock-default-face)

(defconst salsa-font-lock-keywords-2
  (append
   java-font-lock-keywords-2
   (list

    '("\\<\\(behavior\\|module\\)\\>"
      (1 font-lock-keyword-face t)
      (2 font-lock-function-name-face nil t))

    ;; ;; need to fix this to handle: var a, b;
    ;; '("\\<\\(var\\)\\>\\(?:\\s-+\\(\\sw+\\)\\)?"
    ;;   (1 font-lock-keyword-face t)
    ;;   (2 font-lock-variable-name-face nil t))
    ))
  "Medium level highlighting for SALSA mode.")


(defconst salsa-font-lock-keywords-3
  (append
   java-font-lock-keywords-3
   (list

    '("\\<\\(currentContinuation\\|join\\|pass\\|loop\\|repeat\\)\\>"
      (1 font-lock-keyword-face t)
      (2 font-lock-function-name-face nil t))
    ))
  "Gaudy level highlighting for SALSA mode.")

(provide 'salsa-mode)

(defun salsa-indent-line ()
  "Indent current line as SALSA code"
  (interactive)
  ;; (c-indent-line)
  ;; (beginning-of-line)
	(let ((not-indented t) cur-indent)
    (progn
			(save-excursion
			  (forward-line -1)
        (end-of-line)
        ;; (message "char-before: %s" (char-before))
        (setq is-at (equal (char-before) 64)) ;; 64 is @
        (setq is-semicolon (equal (char-before) 59)) ;; 59 is ;
        (if (or is-at is-semicolon)
            (progn
              (setq cur-indent (current-indentation))
              (setq not-indented nil)
              )
          )
        )
      (if not-indented 
          (c-indent-line)
        (indent-line-to cur-indent))
      )
    )
  )


(defun salsa-electric-semi&comma (arg)
  "Insert a comma or semicolon.
It is from cc-cmds.el
If `c-electric-flag' is non-nil, point isn't inside a literal and a
numeric ARG hasn't been supplied, the command performs several electric
actions:

\(a) When the auto-newline feature is turned on (indicated by \"/la\" on
the mode line) a newline might be inserted.  See the variable
`c-hanging-semi&comma-criteria' for how newline insertion is determined.

\(b) Any auto-newlines are indented.  The original line is also
reindented unless `c-syntactic-indentation' is nil.

\(c) If auto-newline is turned on, a comma following a brace list or a
semicolon following a defun might be cleaned up, depending on the
settings of `c-cleanup-list'."
  (interactive "*P")
  (let* (lim literal c-syntactic-context
	 (here (point))
	 ;; shut this up
	 (c-echo-syntactic-information-p nil))

    (c-save-buffer-state ()
      (setq lim (c-most-enclosing-brace (c-parse-state))
	    literal (c-in-literal lim)))

    (self-insert-command (prefix-numeric-value arg))

    (if (and c-electric-flag (not literal) (not arg))
	;; do all cleanups and newline insertions if c-auto-newline is on.
	(if (or (not c-auto-newline)
		(not (looking-at "[ \t]*\\\\?$")))
	    (if c-syntactic-indentation
		(c-indent-line))
	  ;; clean ups: list-close-comma or defun-close-semi
	  (let ((pos (- (point-max) (point))))
	    (if (c-save-buffer-state ()
		  (and (or (and
			    (eq last-command-event ?,)
			    (memq 'list-close-comma c-cleanup-list))
			   (and
			    (eq last-command-event ?\;)
			    (memq 'defun-close-semi c-cleanup-list)))
		       (progn
			 (forward-char -1)
			 (c-skip-ws-backward)
			 (eq (char-before) ?}))
		       ;; make sure matching open brace isn't in a comment
		       (not (c-in-literal lim))))
		(delete-region (point) here))
	    (goto-char (- (point-max) pos)))
	  ;; reindent line
	  (when c-syntactic-indentation
	    (setq c-syntactic-context (c-guess-basic-syntax))
	    (c-indent-line c-syntactic-context))
	  ;; check to see if a newline should be added
	  (let ((criteria c-hanging-semi&comma-criteria)
		answer add-newline-p)
	    (while criteria
	      (setq answer (funcall (car criteria)))
	      ;; only nil value means continue checking
	      (if (not answer)
		  (setq criteria (cdr criteria))
		(setq criteria nil)
		;; only 'stop specifically says do not add a newline
		(setq add-newline-p (not (eq answer 'stop)))
		))
	    (if add-newline-p
		(c-newline-and-indent))
	    ))))
  (salsa-indent-line)
  (end-of-line)
  )

;;; salsa-mode.el ends here
