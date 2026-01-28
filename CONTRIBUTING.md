# Contributing to PromoPing
Thank you for considering contributing to PromoPing! The PromoPing community uses GitHub to receive contributions through issues and pull requests.
To report bugs or suggest improvements, use the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). Check if a similar issue already exists before creating a new one. For bugs, include a descriptive title, detailed description, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment information.
To contribute code, fork the repository, clone your fork, install dependencies (npm install for backend, pip install -r requirements.txt for Python scraper), configure environment variables and the database. Create a branch for your feature or fix, follow the project's code standards (JavaScript/Node.js with ESLint, Python following PEP 8, HTML/CSS with consistent indentation), test your changes, and open a Pull Request with a clear description of the changes.
If you find a bug, check if it has already been reported in the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). If it hasn't been reported, create a new issue with a clear title, detailed description of the problem, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment details (OS, Node.js version, Python, etc.).
Suggestions are always welcome! Check if a similar suggestion already exists in the Issues. Create an issue with the `enhancement` or `feature request` tag, clearly describing the problem the improvement solves, how you imagine it would work, and the benefits for users.

## Contributing Code
**Development Environment Setup**: Fork the repository, clone your fork, install dependencies (npm install for backend, pip install -r requirements.txt for the Python scraper), configure environment variables (copy .env.example to .env if it exists), and configure the database (execute the SQL scripts in sql/).

**Development Process**: Create a branch for your feature/fix (feature/feature-name or fix/bug-name), follow code standards (JavaScript/Node.js with ESLint, Python following PEP 8, HTML/CSS with consistent indentation), write clean code (small, focused functions, descriptive names, comments when necessary, avoid duplication), test your changes manually and verify you haven't broken existing functionality. Make commits following the convention (feat, fix, docs, style, refactor, test, chore), push to your fork, and open a Pull Request with a clear description of the changes, related issues (if any), screenshots (if applicable), and verification checklist.
To better understand how the PromoPing project is managed and how to collaborate, we recommend reviewing [README.md](README.md) and the submission guidelines.

## Code Standards
**JavaScript/Node.js**: Use const by default, let when necessary, avoid var. Use arrow functions when appropriate. Prefer async/await. Variables and functions in camelCase; classes in PascalCase. Semicolon at the end of lines. Indentation: 2 spaces.
**Python**: Follow PEP 8. Use type hints when possible. Docstrings for functions and classes. snake_case for functions/variables, PascalCase for classes. Indentation: 4 spaces.
**HTML/CSS**: Consistent indentation (2 spaces). Use semantic attributes. Comment complex sections. Use descriptive classes (BEM when appropriate).

## Pull Request Checklist
Before submitting a PR, verify: code follows project standards, functionality tested manually, no lint/console errors, documentation updated (if necessary), commits follow the convention, branch is up to date with main/master, PR has a clear description and screenshots (if applicable).

## Review Process
Maintainers will review your PR by checking if the code follows standards, testing the changes, and suggesting improvements if necessary. Respond to comments, make requested changes, and update the PR as needed. After approval, the PR will be merged and you will be credited as a contributor.

## Questions?
If you have questions about how to contribute, open an issue with the `question` tag or contact us at <corporation.promoping@gmail.com>
Your friendly PromoPing community!
Thank you for contributing to make PromoPing better!
